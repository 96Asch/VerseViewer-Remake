package com.verseviewer.application.controller

import com.verseviewer.application.model.Passage
import com.verseviewer.application.model.Preference
import javafx.collections.FXCollections
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import javafx.stage.Screen
import tornadofx.Controller
import tornadofx.asObservable

class ProjectionEditorController : Controller() {

    internal data class IndexedScreen(val index : Int, val screen: Screen)

    private val dbController : DBController by inject()
    internal val screenList = Screen.getScreens().mapIndexed { index, screen ->  IndexedScreen(index, screen)}

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

    fun savePreferencesToDB(pref : Preference) {
        dbController.updateUserPreference(pref)
    }
}
