package com.verseviewer.application.controller

import com.verseviewer.application.model.Translation
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

class HelpPaneController : Controller() {

    private val dbController : DBController by inject()
    val translations: ObservableList<Translation> = FXCollections.observableArrayList(dbController.getTranslations())

    val field1Text = listOf("Setting Translation"
            , "To set a translation, type \".t\" followed by the abbreviated translation name, e.g:"
            , ".t ASV")

    val field2Text = listOf("Populating Verse Table"
            , "Type the verses in the standard format to populate the table:"
            , "John 3:16, Mat1, Gen"
            ,"If only the book is specified then the table will be populated with all verses of that book. " +
             "If the table is populated then the Chapter:Verse (e.g 3:16) " +
             "format can be used to filter or search for the verse in the table. " +
             "Multiple verses can be searched for by adding numbers (1,2,3) or with a range (1-3) e.g:"
            , "1:1-2,3,5-7")

    val field3Text = listOf("Multiple Selection"
            , "Multiple verses can be selected by using the CTRL and SHIFT buttons. " +
              "Verses will only be sent to the projection when the key is released.")

    val field4Text = listOf("Editing verses"
            , "Verses can be edited by double clicking on them. The edited verse will be updated in the database.")
}