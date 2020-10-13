package com.verseviewer.application.controller

import com.verseviewer.application.model.Passage
import com.verseviewer.application.model.datastructure.VerseGroup
import javafx.collections.ObservableList
import javafx.scene.SnapshotParameters
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.*
import tornadofx.*


class ScheduleController : Controller() {

    val list = mutableListOf<VerseGroup>().asObservable()
    val detailList = mutableListOf<Passage>().asObservable()

    private var dragVerse : VerseGroup? = null

    fun setAll(l: List<VerseGroup>) {
        list.setAll(l)
    }

    fun setDetail(vg: VerseGroup) {
        detailList.setAll(vg.verses)
    }

    fun dragDetected(evt: MouseEvent, row : TableRow<VerseGroup>) {
        if (row.item != null) {
            val dragboard = row.startDragAndDrop(TransferMode.MOVE)
            val content = ClipboardContent()
            dragVerse = row.item
            content.putString(row.item.toString())
            dragboard.dragView = row.snapshot(null, WritableImage(row.width.toInt(), row.height.toInt()))
            dragboard.setContent(content)
            evt.consume()
        }
    }

    fun dragOver(evt: DragEvent) {
        if (evt.gestureSource != this && evt.dragboard.hasString()) {
            evt.acceptTransferModes(TransferMode.MOVE);
        }
    }

    fun dragEntered(evt: DragEvent, row : TableRow<VerseGroup>) {
        if (evt.gestureSource != this && evt.dragboard.hasString()) {
            row.opacity = 0.3;
        }
    }

    fun dragExited(evt: DragEvent, row : TableRow<VerseGroup>) {
        if (evt.gestureSource != this && evt.dragboard.hasString()) {
            row.opacity = 1.0;
        }
    }

    fun dragDropped(evt: DragEvent, row : TableRow<VerseGroup>) {
        if (row.item != null) {
            var success = false

            if (dragVerse != null) {
                val draggedIdx = list.indexOf(dragVerse)
                val thisIdx = list.indexOf(row.item)
                list.moveAt(draggedIdx, thisIdx)
                row.tableView.selectionModel.clearAndSelect(thisIdx)
                dragVerse = null
                success = true
            }
            evt.isDropCompleted = success
        }
    }

    fun dragDone(evt: DragEvent) {
        evt.consume()
    }

    fun groupSelected(tv: TableView<VerseGroup>) {
        if (tv.items.isEmpty()) return

        val selectModel = tv.selectionModel
        val firstIndex = selectModel.selectedIndices.first()
        selectModel.selectedIndices.forEach {
            if (it != firstIndex) {
                list[firstIndex].merge(list[it])
            }
        }
        selectModel.selectedIndices.reversed().forEach {
            if (it != firstIndex)
                list.removeAt(it)
        }
        selectModel.clearAndSelect(firstIndex)
        detailList.setAll(tv.selectedItem?.verses)
    }

    //todo: Allow multi selection
    fun ungroupSelected(tv: TableView<VerseGroup>) {
        if (tv.items.isEmpty()) return
        val selectModel = tv.selectionModel
        if (selectModel.selectedIndices.isEmpty()) return

        val firstIndex = selectModel.selectedIndices.first()
        if (list[firstIndex].verses.size > 1) {
            val newList = list[firstIndex].verses.map { v ->
                VerseGroup(mutableListOf(v))
            }
            list.removeAt(firstIndex)
            list.addAll(firstIndex, newList)
        }

        selectModel.clearAndSelect(firstIndex)
        detailList.setAll(tv.selectedItem?.verses)
    }

    fun deleteSelected(tv: TableView<VerseGroup>) {
        if (tv.items.isEmpty()) return
        val selectModel = tv.selectionModel
        if (selectModel.selectedIndices.isEmpty()) return

        val toRemove = selectModel.selectedItems
        list.removeAll(toRemove)
        if (list.isEmpty())
            detailList.clear()
    }

}