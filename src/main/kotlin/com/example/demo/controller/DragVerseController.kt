package com.example.demo.controller

import com.example.demo.model.Verse
import com.example.demo.model.datastructure.GroupType
import com.example.demo.model.datastructure.VerseGroup
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

    val isDrag = SimpleBooleanProperty(false)


    fun dragStart(evt: MouseEvent, tv : TableView<Verse>) {
        if (tv.selectionModel.selectedItems.isNotEmpty()) {
            val vg = VerseGroup(tv.selectionModel.selectedItems, GroupType.MONO_TRANSLATION)
            dragGroup = vg
            isDrag.value = true

            val db = tv.startDragAndDrop(TransferMode.COPY)
            val cc = ClipboardContent()
            cc.putString(vg.verses.joinToString(separator = "\n") { it.toString() })
            db.setContent(cc)

        }
        evt.consume()
    }

    fun dragEntered(evt: DragEvent, node : Node) {
        if (evt.gestureSource != node && isDrag.value) {

        }
        evt.consume()
    }

    fun dragExited(evt : DragEvent) {
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
            println("Add $dragGroup")
            list.add(dragGroup!!)
            dragGroup = null
        }
        evt.isDropCompleted = success
        evt.consume()
    }


}