package com.verseviewer.application.controller

import com.verseviewer.application.model.Translation
import com.verseviewer.application.model.TranslationModel
import com.verseviewer.application.model.Verse
import com.verseviewer.application.model.datastructure.BookTrie
import com.verseviewer.application.model.datastructure.Range
import com.verseviewer.application.model.datastructure.TranslationTrie
import com.verseviewer.application.model.datastructure.inRange
import com.verseviewer.application.model.event.NotificationType
import com.verseviewer.application.model.event.SendNotification
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

class VerseSearchController : Controller() {

    private val regex = """^([1-3]?\s*[a-zA-Z]+)?(\s?\d{1,3})?\s?(?::)?\s?((?<=:)\d{1,3})?""".toRegex()
    private val dbController : DBController by inject()
    private val tableController : VerseBoxController by inject()

    private val translationModel : TranslationModel by inject()

    private val bookTrie = BookTrie()
    private val translationTrie = TranslationTrie()

    private val errorDuration = 2
    private val warningDuration = 2

    val filterModeProperty = SimpleBooleanProperty(false)

    fun processText(text: String, list : MutableList<Verse>) : Int {
        return when {
            text.startsWith(".t") -> {
                setTranslation(text)
                0
            }
            text.startsWith(".h") -> {
                fire(SendNotification("", NotificationType.HELP, 0))
                0
            }
            else -> retrieveVerses(text, list)
        }
    }

    fun updateBookTrie(translation : Translation) {
        bookTrie.clear()
        dbController.getBooksByTranslation(translation.name).forEach {
            bookTrie.insert(it)
        }
    }

    private fun setTranslation(text: String) {
        val token = text.substring(2).replace("\\s".toRegex(), "")

        val list = translationTrie.retrieve(token)

        when (list.isEmpty()) {
            true -> {
                fire(SendNotification("Unknown translation: $token", NotificationType.ERROR, errorDuration))
            }
            else -> {
                translationModel.item = list.first()
                translationModel.commit()
                updateBookTrie(list.first())
                tableController.swapVersesByTranslation(list.first().name)

            }
        }
    }

    private fun validate(text: String) : Boolean {
        return when {
            text.isEmpty() ->  {
                fire(SendNotification("Input was empty", NotificationType.ERROR, errorDuration))
                false
            }
            translationModel.name.value.isNullOrEmpty()  -> {
                fire(SendNotification("Translation is not set", NotificationType.ERROR, errorDuration))
                false
            }
            else -> true
        }
    }

    private fun retrieveVerses(text: String, list : MutableList<Verse>) : Int {
        if (!validate(text)) return 0


        val matchResult = regex.find(text)!!
        val (book, chapter, verse) = matchResult.destructured
        val flag = getFlag(matchResult.groupValues.subList(1, matchResult.groupValues.size))

        list.clear()
        list.addAll(getBookVerses(book))

        val ch = chapter.replace("\\s".toRegex(), "")
        val v = verse.replace("\\s".toRegex(), "")

        if (filterModeProperty.value) {
            list.filter {
                when(flag) {
                    0b111, 0b011 -> (it.chapter == ch.toInt() && v.toInt() inRange it.verse)
                    0b110        -> it.chapter == ch.toInt()
                    0b101, 0b001 -> v.toInt() inRange it.verse
                    0b010        -> ch.toInt() inRange it.verse
                    else -> true
                }
            }
        }

        return list.indexOfFirst {
            when (flag) {
                0b111, 0b011 -> (it.chapter == ch.toInt() && v.toInt() inRange it.verse)
                0b110        -> it.chapter == ch.toInt()
                0b101, 0b001 -> v.toInt() inRange it.verse
                0b010        -> ch.toInt() inRange it.verse
                else -> true
            }
        }
    }


    private fun getFlag(list: List<String>) : Int {
        var result = 0b000
        list.reversed().forEachIndexed { index, s ->
            if (s.isEmpty().not())
                result = result or (1 shl index)
        }
        return result
    }

    private fun getBookVerses(book : String) : List<Verse> {
        if (book.isEmpty().not()) {
            val bookMap = bookTrie.retrieve(book)
            val list = mutableListOf<Verse>()

            if (bookMap.isEmpty()) {
                fire(SendNotification("No book found with prefix \"$book\"", NotificationType.WARNING, warningDuration))
            }

            bookMap.forEach{
                list.addAll(dbController.getBookVerses(translationModel.name.value, it.value))
            }
            tableController.setCache(list)
        }
        return tableController.getCache()
    }


    init {
        dbController.getTranslations().forEach {
            translationTrie.insert(it)
        }
    }
}