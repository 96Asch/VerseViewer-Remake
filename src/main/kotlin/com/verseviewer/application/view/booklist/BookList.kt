package com.verseviewer.application.view.booklist

import com.verseviewer.application.controller.BookListController
import com.verseviewer.application.model.Book
import com.verseviewer.application.model.Translation
import com.verseviewer.application.model.TranslationModel
import tornadofx.*

class BookList() : Fragment("BookList") {

    private val translationModel : TranslationModel by inject()
    private val controller : BookListController by inject()

    private val translationListener = ChangeListener<Translation> { _, old, new ->
        if (old != new) {
            controller.populateBooks(new)
        }
    }
    private val selectedBookListener = ChangeListener<Book> { _, old, new ->
        if (old != new) {
            controller.sendVerses(translationModel.name.value, new.book_id)
        }
    }
    private val listView = listview(values = controller.bookList)

    override val root = vbox {
        combobox(values = controller.translationList) {
            cellFormat {
                text = it.abbreviation
            }
            valueProperty().bindBidirectional(translationModel.itemProperty)
        }

        this += listView.apply {
            cellFormat {
                text = it.name
            }
            selectionModel.selectedItemProperty().addListener(selectedBookListener)
        }
    }

    override fun onUndock() {
        super.onUndock()
        subscribedEvents.clear()
        translationModel.itemProperty.removeListener(translationListener)
        listView.selectionModel.selectedItemProperty().removeListener(selectedBookListener)
    }


    init {
        translationModel.itemProperty.addListener(translationListener)
    }
}