package com.verseviewer.application.controller

import com.verseviewer.application.model.Book
import com.verseviewer.application.model.Passage
import com.verseviewer.application.model.Translation
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*

class VerseBoxController : Controller() {

    val translationProperty = SimpleObjectProperty<Translation>()
    val translation: Translation? by translationProperty
    val verseList = mutableListOf<Passage>().asObservable()

    private var verseCache = listOf<Passage>()
    private val dbController : DBController by inject()

    fun swapVersesByTranslation(translation: String) {
        if (verseList.isNotEmpty()) {
            val list = dbController.exchangeVersesByTranslation(verseCache, translation)
            val ids = verseList.map { it.id }
            val filtered = list.filter { it.id in ids }
            verseCache = list
            verseList.setAll(filtered)
        }
    }

    fun setBookVerses(book : Book) {
        if (translation != null) {
            val verses = dbController.getBookVerses(translation!!.name, book.book_id)
            verseList.setAll(verses)
            setCache(verses)
        }
    }

    fun get(ids : List<Int>) : List<Passage> {
        return verseCache.filter { it.id in ids }
    }

    fun setCache(list : List<Passage>) {
        verseCache = list
    }

    fun getCache() : List<Passage>  = verseCache.toList()
}