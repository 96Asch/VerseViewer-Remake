package com.verseviewer.application.view.booklist

import com.verseviewer.application.model.TranslationModel
import javafx.scene.Parent
import tornadofx.*
import javax.swing.text.html.ListView

class BookList() : View("BookList") {

    private val translationModel : TranslationModel by inject()

    override val root = hbox {
        

    }
}