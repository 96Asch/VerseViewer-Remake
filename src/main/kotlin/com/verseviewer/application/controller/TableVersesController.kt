package com.verseviewer.application.controller

import com.verseviewer.application.model.TableVersesModel
import com.verseviewer.application.model.Verse
import tornadofx.*

class TableVersesController : Controller() {

    private val tableModel : TableVersesModel by inject()
    private val dbController : DBController by inject()

    fun swapVersesByTranslation(translation: String) {
        if (tableModel.cache.isNotEmpty()) {
            val list = dbController.exchangeVersesByTranslation(tableModel.cache, translation)
            val ids = tableModel.verses.map { it.id }
            val filtered = list.filter { it.id in ids }
            tableModel.cache = list
            tableModel.verses.setAll(filtered)
        }
    }

    fun get(ids : List<Int>) : List<Verse> {
        return tableModel.cache.filter { it.id in ids }
    }

    fun setCache(list : List<Verse>) {
        tableModel.cache = list
    }

    fun getCache() : List<Verse>  = tableModel.cache


}