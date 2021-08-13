package com.verseviewer.application.controller

import com.verseviewer.application.model.Passage
import com.verseviewer.application.model.Snapshot
import javafx.beans.property.SimpleIntegerProperty
import javafx.stage.Screen
import tornadofx.Controller

class ProjectionEditorController : Controller() {

    internal data class IndexedScreen(val index : Int, val screen: Screen)

    private val dbController : DBController by inject()
    internal val screenList = Screen.getScreens().mapIndexed { index, screen ->  IndexedScreen(index, screen)}

    val numTranslationsProperty = SimpleIntegerProperty(0)
    val numVersesProperty = SimpleIntegerProperty(0)

    private val translationList by lazy { dbController.getTranslations() }

    fun loadTestVerses() : List<Passage> {
        val resultList = mutableListOf<Passage>()
        for (i in 0 until numTranslationsProperty.value) {
            val bookVerses = dbController.getBookVerses(translationList[i].name, 1)
            resultList.addAll(bookVerses.filter { it.verse.first <= numVersesProperty.value && it.chapter == 1 })
        }
        return resultList
    }

    fun savePreferencesToDB(pref : Snapshot) {
        dbController.updateUserPreference(pref)
    }
}
