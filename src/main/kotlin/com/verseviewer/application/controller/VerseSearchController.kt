package com.verseviewer.application.controller

import com.verseviewer.application.model.Translation
import com.verseviewer.application.model.Passage
import com.verseviewer.application.model.datastructure.BookTrie
import com.verseviewer.application.model.datastructure.Range
import com.verseviewer.application.model.datastructure.TranslationTrie
import com.verseviewer.application.model.datastructure.inRange
import com.verseviewer.application.model.event.BroadcastTranslation
import com.verseviewer.application.model.event.BroadcastVBHelp
import com.verseviewer.application.model.event.NotificationType
import com.verseviewer.application.model.event.SendNotification
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*
import java.lang.NumberFormatException

class VerseSearchController : Controller() {

    private val regex = """^([1-3]?\s*[a-zA-Z]+)?\s*(\d{1,3})?\s*(?::)?\s*(\d{1,3}(?:(?:\s*-\s*\d{1,3})|(?:\s*,\s*\d{1,3}))*)?""".toRegex()
    private val dbController : DBController by inject()
    private val tableController : VerseBoxController by inject()

    private val bookTrie = BookTrie()
    private val translationTrie = TranslationTrie()

    private val errorDuration = 2
    private val warningDuration = 2

    val filterModeProperty = SimpleBooleanProperty(false)

    fun processText(text: String, translation : Translation?, list : MutableList<Passage>) : Int {
        return when {
            text.startsWith(".t") -> {
                lookupTranslation(text)
                -1
            }
            text.startsWith(".h") -> {
                fire(BroadcastVBHelp())
                -1
            }
            else -> retrieveVerses(text, translation, list)
        }
    }

    fun updateBookTrie(translation : Translation) {
        bookTrie.clear()
        dbController.getBooksByTranslation(translation.name).forEach {
            bookTrie.insert(it)
        }
    }

    private fun lookupTranslation(text: String) {
        val token = text.substring(2).replace("\\s".toRegex(), "")
        val list = translationTrie.retrieve(token)

        if (list.isEmpty())
            fire(SendNotification("Unknown translation: $token", NotificationType.ERROR, errorDuration))
        else
            fire(BroadcastTranslation(list.first()))
    }

    private fun validate(text: String, translation : Translation?) : Boolean {
        return when {
            text.isEmpty() ->  {
                fire(SendNotification("Input was empty", NotificationType.ERROR, errorDuration))
                false
            }
            translation == null  -> {
                fire(SendNotification("Translation is not set", NotificationType.ERROR, errorDuration))
                false
            }
            else -> true
        }
    }

    private fun retrieveVerses(text: String, translation : Translation?, list : MutableList<Passage>) : Int {
        if (!validate(text, translation)) return 0

        val matchResult = regex.find(text)!!
        val (book, chapter, verse) = matchResult.destructured
        val flag = getFlag(matchResult.groupValues.subList(1, matchResult.groupValues.size))

        list.clear()
        list.addAll(getBookVerses(translation!!.name, book))

        val (ch, v) = convertPassageIndex(chapter.replace("\\s".toRegex(), ""), verse.replace("\\s".toRegex(), ""))

        if (filterModeProperty.value)
            list.removeAll { !filterVersesPredicate(flag, ch, v, it) }

        return list.indexOfFirst { filterVersesPredicate(flag, ch, v, it) }
    }

    private fun convertPassageIndex(chapterStr : String, verseStr : String) : Pair<Int, List<Int>> {
        var chapter = 0
        try {
            chapter = chapterStr.toInt()
        } catch (e : NumberFormatException) {
            e.printStackTrace()
        }
        return Pair(chapter, verseStringToList(verseStr))
    }

    private fun verseStringToList(verseStr : String) : List<Int> {
        val list = mutableListOf<Int>()
        val splitRanges = verseStr.split(',').apply {
            forEach { it.replace("\\s".toRegex(), "") }
        }

        try {
            splitRanges.forEach {
                val token = it.replace("\\s".toRegex(), "")
                if (token.contains('-')) {
                    val verseIndices = token.split('-').map { rangeIndex -> rangeIndex.toInt() }
                    list.addAll(verseIndices.first() until  verseIndices.last()+1)
                }
                else
                    list.add(token.toInt())
            }
        }
        catch (e : NumberFormatException) {
            e.printStackTrace()
        }

        return list
    }

    private fun filterVersesPredicate(flag : Int, chapterIndex : Int, verseIndices : List<Int>, passage : Passage): Boolean {
       return when (flag) {
            0b111, 0b011 -> (passage.chapter == chapterIndex && passage.verse.first() in verseIndices)
            0b110        -> passage.chapter == chapterIndex
            0b101, 0b001 -> passage.verse.first() in verseIndices
            0b010        -> chapterIndex inRange passage.verse
            else -> true
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

    private fun getBookVerses(translation : String, book : String) : List<Passage> {
        if (book.isEmpty().not()) {
            val bookMap = bookTrie.retrieve(book)
            val list = mutableListOf<Passage>()

            if (bookMap.isEmpty()) {
                fire(SendNotification("No book found with prefix \"$book\"", NotificationType.WARNING, warningDuration))
            }

            bookMap.forEach{
                list.addAll(dbController.getBookVerses(translation, it.value))
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