package com.verseviewer.application.controller

import com.verseviewer.application.model.Passage
import tornadofx.Controller
import tornadofx.asObservable

class VerseBoxController : Controller() {

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

    fun get(ids : List<Int>) : List<Passage> {
        return verseCache.filter { it.id in ids }
    }

    fun setCache(list : List<Passage>) {
        verseCache = list
    }

    fun getCache() : List<Passage>  = verseCache
}