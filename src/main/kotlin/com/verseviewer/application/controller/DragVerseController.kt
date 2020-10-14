package com.verseviewer.application.controller

import com.verseviewer.application.model.Passage
import com.verseviewer.application.model.VerseGroup
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.control.TableView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import tornadofx.*

class DragVerseController : Controller() {

    private var dragGroup : VerseGroup? = null
    private val isDrag = SimpleBooleanProperty(false)

    fun dragStart(evt: MouseEvent, tv : TableView<Passage>) {
        if (tv.selectionModel.selectedItems.isNotEmpty()) {
            val vg = VerseGroup(tv.selectionModel.selectedItems)
            dragGroup = vg
            isDrag.value = true

            val db = tv.startDragAndDrop(TransferMode.COPY)
            val cc = ClipboardContent()
            cc.putString(vg.verses.joinToString(separator = "\n") { it.toString() })
            db.setContent(cc)

        }
        evt.consume()
    }

    fun dragOver (evt: DragEvent, node: Node) {
        if (evt.gestureSource != node && isDrag.value) {
            evt.acceptTransferModes(TransferMode.COPY)
        }
        evt.consume()
    }

    fun dragDrop(evt: DragEvent, list: MutableList<VerseGroup>) {
        var success = false

        if (isDrag.value && dragGroup != null) {
            success = true
            isDrag.value = false
            list.add(dragGroup!!)
            dragGroup = null
        }
        evt.isDropCompleted = success
        evt.consume()
    }


}