package com.example.demo.controller

import com.example.demo.model.TranslationModel
import com.example.demo.model.Verse
import com.example.demo.model.datastructure.BookTrie
import com.example.demo.model.datastructure.TranslationTrie
import com.example.demo.model.datastructure.inRange
import com.example.demo.model.event.NotificationType
import com.example.demo.model.event.SendNotification
import tornadofx.*

class VerseSearchController : Controller() {

    private val regex = """^([1-3]?\s*[a-zA-Z]+)?(\s?\d{1,3})?\s?(?::)?\s?((?<=:)\d{1,3})?""".toRegex()
    private val dbController : DBController by inject()
    private val tableController : TableVersesController by inject()

    private val translationModel : TranslationModel by inject()

    private val bookTrie = BookTrie()
    private val translationTrie = TranslationTrie()

    private val errorDuration = 2
    private val warningDuration = 2

    fun processText(text: String) : List<Verse> {
        return when {
            text.startsWith(".t") -> {
                setTranslation(text)
                listOf<Verse>()
            }
            text.startsWith(".h") -> {
                fire(SendNotification("", NotificationType.HELP, 0))
                listOf<Verse>()
            }
            else -> retrieveVerses(text)
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
                translationModel.name.value = list.first()
                bookTrie.clear()
                dbController.getBooksByTranslation(list.first()).forEach {
                    bookTrie.insert(it)
                }
                tableController.swapVersesByTranslation(list.first())

            }
        }
    }

    private fun validate(text: String) : Boolean {
        return when {
            text.isNullOrEmpty() ->  {
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

    private fun retrieveVerses(text: String) : List<Verse> {
        if (!validate(text)) return listOf<Verse>()

        val matchResult = regex.find(text)!!
        val (book, chapter, verse) = matchResult.destructured
        val flag = getFlag(matchResult.groupValues.subList(1, matchResult.groupValues.size))
        val list = getBookVerses(book)

        val ch = chapter.replace("\\s".toRegex(), "")
        val v = verse.replace("\\s".toRegex(), "")

        return list.filter {
            when(flag) {
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
            if (s.isNullOrEmpty().not())
                result = result or (1 shl index)
        }
        return result
    }

    private fun getBookVerses(book : String) : List<Verse> {
        if (book.isNullOrEmpty().not()) {
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