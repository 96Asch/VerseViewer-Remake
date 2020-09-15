package com.verseviewer.application.view.booklist

import com.verseviewer.application.controller.BookListController
import com.verseviewer.application.model.Book
import com.verseviewer.application.model.Translation
import com.verseviewer.application.model.TranslationModel
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.layout.Priority
import tornadofx.*

class BookList() : Fragment("BookList") {

    private val translationModel : TranslationModel by inject()
    private val controller : BookListController by inject()

    private var lastIndex = 0

    private val listView = listview(values = controller.bookList)

    private val translationListener = ChangeListener<Translation> { _, old, new ->
        if (old != new) {
            controller.populateBooks(new)
            listView.selectionModel.clearAndSelect(lastIndex)
        }
    }

    private val selectedBookListener = ChangeListener<Book> { _, old, new : Book? ->
        if (new != null && translationModel.isNotEmpty) {
            lastIndex = listView.selectionModel.selectedIndex
            controller.sendVerses(translationModel.item.name, new.book_id)
        }
    }

    override val root = vbox {
        combobox(property = translationModel.itemProperty, values = controller.translationList)

        this += listView.apply {
            cellFormat {
                text = it.name
            }
            selectionModel.selectedItemProperty().addListener(selectedBookListener)
            vboxConstraints { vGrow = Priority.ALWAYS }
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