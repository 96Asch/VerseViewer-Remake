package com.verseviewer.application.controller

import com.verseviewer.application.model.Passage
import javafx.collections.FXCollections
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import tornadofx.Controller

class ProjectionEditorController : Controller() {

    private val dbController : DBController by inject()

    private val translationList by lazy { dbController.getTranslations() }

    fun getTestVerses(numTranslations : Int, numVerses : Int) : List<Passage> {
        val resultList = mutableListOf<Passage>()
        println(numVerses)
        for (i in 0 until numTranslations) {
            val bookVerses = dbController.getBookVerses(translationList[i].name, 1)
            resultList.addAll(bookVerses.filter { it.verse.first() <= numVerses && it.chapter == 1 })
        }
        return resultList
    }
}