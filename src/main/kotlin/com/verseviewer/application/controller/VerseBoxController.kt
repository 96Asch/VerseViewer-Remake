package com.verseviewer.application.controller

import com.verseviewer.application.model.Translation
import com.verseviewer.application.model.Verse
import javafx.beans.property.SimpleObjectProperty
import tornadofx.Controller
import tornadofx.asObservable

class VerseBoxController : Controller() {

    val verseList = mutableListOf<Verse>().asObservable()
    private var verseCache = listOf<Verse>()

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

    fun get(ids : List<Int>) : List<Verse> {
        return verseCache.filter { it.id in ids }
    }

    fun setCache(list : List<Verse>) {
        verseCache = list
    }

    fun getCache() : List<Verse>  = verseCache
}