package com.verseviewer.application.view.components.booklist

import com.verseviewer.application.controller.BookListController
import com.verseviewer.application.model.Translation
import com.verseviewer.application.model.event.BroadcastBook
import com.verseviewer.application.model.event.BroadcastTranslation
import com.verseviewer.application.model.event.DeselectBook
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.Priority
import tornadofx.*

class BookList : Fragment("BookList") {

    private val controller : BookListController by inject()
    private var lastIndex = -1

    override val root = vbox {
        val translationProperty = SimpleObjectProperty<Translation>()

        combobox(values = controller.translationList) {
            selectionModel.selectedItemProperty().onChange {
                if (it != null) {
                    controller.populateBooks(it)
                    fire(BroadcastTranslation(it))
                }
            }
            valueProperty().bindBidirectional(translationProperty)
        }

        listview(values = controller.bookList) {
            cellFormat { text = it.name }
            vboxConstraints { vGrow = Priority.ALWAYS }

            selectionModel.selectedItemProperty().onChange {
                if (it != null) {
                    lastIndex = selectionModel.selectedIndex
                    fire(BroadcastBook(it))
                }
            }

            subscribe<BroadcastTranslation> {
                controller.populateBooks(it.translation)
                if (lastIndex >= 0)
                    selectionModel.clearAndSelect(lastIndex)
                translationProperty.value = it.translation
            }

            subscribe<DeselectBook> {
                selectionModel.clearSelection()
            }
        }
    }
}