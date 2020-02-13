package com.example.demo.view.schedule

import com.example.demo.controller.DragVerseController
import com.example.demo.controller.TableVersesController
import com.example.demo.model.DisplayVersesModel
import com.example.demo.model.Verse
import com.example.demo.model.datastructure.GroupType
import com.example.demo.model.datastructure.VerseGroup
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.control.TableView
import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import kotlinx.coroutines.selects.select
import tornadofx.*
import tornadofx.controlsfx.detail
import tornadofx.controlsfx.hiddensidepane
import tornadofx.controlsfx.master
import tornadofx.controlsfx.masterdetailpane

class Schedule : View("My View") {
    private val scheduleList = mutableListOf<VerseGroup>().observable()
    private val lvList = FXCollections.observableArrayList<Verse>()

    private val displayModel : DisplayVersesModel by inject()

    private val dragVerseController : DragVerseController by inject()

    private val tv = tableview(scheduleList)

    override val root = masterdetailpane {

         master {
             hiddensidepane {
                 tv.apply {
                     readonlyColumn("Verse", VerseGroup::verses) {
                         isSortable = false
                         cellFormat {
                             graphic = text {
                                 text = it.joinToString { "${it.translation.abbreviation} ${it.book} ${it.chapter}:${it.verse}" }
                                 wrappingWidthProperty().bind(this@apply.widthProperty().subtract(20))
                             }
                         }
                         remainingWidth()
                     }
                     bindSelected(displayModel)
                     selectionModel.selectedItemProperty().addListener{_, _, new ->
                         if (new != null) {
                             lvList.setAll(new.verses)
                         }
                     }

                     setOnDragDropped(::dragDrop)
                     setOnDragOver(::dragOver)
                     multiSelect(true)
                     smartResize()
                     hboxConstraints {
                         hgrow = Priority.ALWAYS
                     }
                 }

                 content = tv
                 right = vbox {
                     paddingAll = 10
                     paddingTop = 30
                     button("Up") {
                         action {
                             moveSelectedUp(tv)
                             tv.requestFocus()
                         }
                     }
                     button("Down") {
                         action {
                             moveSelectedDown(tv)
                             tv.requestFocus()
                         }
                     }
                     button("Group") {
                         action {
                             group(tv)
                             tv.requestFocus()
                         }
                     }
                     button("Ungroup") {
                         action {
                             ungroup(tv)
                             tv.requestFocus()
                         }
                     }
                 }
                 triggerDistance = 80.toDouble()
             }

         }

        detail {
            listview(lvList) {
                cellFormat {
                    graphic = form {
                        fieldset("${it.translation.abbreviation} ${it.book} ${it.chapter} : ${it.verse}") {
                            text("${it.text}") {
                                wrappingWidthProperty().bind(this@listview.widthProperty().subtract(50))
                            }
                        }
                    }
                    isMouseTransparent = true
                    isFocusTraversable = false
                }
            }
        }

        dividerPosition = 0.4
        showDetailNodeProperty().bind(lvList.sizeProperty.greaterThan(0).and(hoverProperty()))
        detailSide = Side.BOTTOM
    }

    private fun moveSelectedUp(tv : TableView<VerseGroup>) {
        val selectModel = tv.selectionModel
        val indices = selectModel.selectedIndices.toMutableList()
        val items = scheduleList

        selectModel.clearSelection()
        indices.forEachIndexed { i, selectedIndex ->
            if (selectedIndex > 0) {
                if (selectedIndex - 1 !in indices) {
                    items.swap(selectedIndex, selectedIndex - 1)
                    indices[i]--
                }
            }
        }
        indices.forEach { selectModel.select(it) }
    }

    private fun moveSelectedDown(tv : TableView<VerseGroup>) {
        val selectModel = tv.selectionModel
        val lastIndex = tv.items.size - 1
        val indices = selectModel.selectedIndices.reversed().toMutableList()
        val items = scheduleList

        selectModel.clearSelection()
        indices.forEachIndexed { i, selectedIndex ->
            println("$indices, $selectedIndex")
            if (selectedIndex < lastIndex) {
                if (selectedIndex + 1 !in indices) {
                    items.swap(selectedIndex, selectedIndex + 1)
                    indices[i]++
                }
            }
        }
        indices.forEach { selectModel.select(it) }
    }

    private fun group(tv: TableView<VerseGroup>) {
        if (tv.items.isEmpty()) return

        val selectModel = tv.selectionModel
        val firstIndex = selectModel.selectedIndices.first()
        val items = scheduleList
        selectModel.selectedIndices.forEach {
            println(items)
            if (it != firstIndex) {
                items[firstIndex].merge(items[it])
            }
        }
        selectModel.selectedIndices.reversed().forEach {
            if (it != firstIndex)
                items.removeAt(it)
        }
        selectModel.clearAndSelect(firstIndex)
        lvList.setAll(tv.selectedItem?.verses)
    }

    //todo: Allow multi selection
    private fun ungroup(tv: TableView<VerseGroup>) {
        if (tv.items.isEmpty()) return
        val selectModel = tv.selectionModel
        if (selectModel.selectedIndices.isEmpty()) return

        val items = scheduleList
        val firstIndex = selectModel.selectedIndices.first()
        if (items[firstIndex].verses.size > 1) {
            val newList = items[firstIndex].verses.map {v ->
                VerseGroup(mutableListOf(v), GroupType.MONO_TRANSLATION)
            }
            items.removeAt(firstIndex)
            items.addAll(firstIndex, newList)
        }

        selectModel.clearAndSelect(firstIndex)
        lvList.setAll(tv.selectedItem?.verses)
    }

    private fun dragOver(evt: DragEvent) {
        println("Drag Entered")
        dragVerseController.dragOver(evt, tv)
    }

    private fun dragDrop(evt: DragEvent) {
        println("Drag drop")
        dragVerseController.dragDrop(evt, scheduleList)
    }
 }
