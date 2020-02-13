package com.example.demo.controller

import com.example.demo.model.TableVersesModel
import com.example.demo.model.Translation
import com.example.demo.model.Verse
import com.example.demo.model.event.RequestColumnResize
import javafx.beans.property.SimpleObjectProperty
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