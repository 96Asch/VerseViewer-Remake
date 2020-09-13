package com.verseviewer.application.view.booklist

import com.verseviewer.application.controller.BookListController
import com.verseviewer.application.model.BookModel
import com.verseviewer.application.model.TranslationModel
import javafx.scene.Parent
import tornadofx.*
import javax.swing.text.html.ListView

class BookList() : Fragment("BookList") {

    private val translationModel : TranslationModel by inject()
    private val bookModel : BookModel by inject()
    private val controller : BookListController by inject()

    override val root = vbox {
        combobox(values = controller.translationList) {
            cellFormat {
                text = it.abbreviation
            }
            valueProperty().bindBidirectional(translationModel.itemProperty)
        }
        listview(values = controller.bookList) {
            bindSelected(bookModel)
            cellFormat {
                text = it.name
            }
        }

    }

    init {
        translationModel.itemProperty.addListener { _, oldValue, newValue ->
            if (oldValue != newValue && newValue != null )
                controller.populateBooks(newValue)
        }
        bookModel.itemProperty.onChange {
            if (it != null) {
                println(it.name)
            }
        }
    }
}