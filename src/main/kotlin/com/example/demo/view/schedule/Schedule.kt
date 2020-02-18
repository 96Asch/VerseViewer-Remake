package com.example.demo.view.schedule

import tornadofx.*
import com.example.demo.controller.DragVerseController
import com.example.demo.controller.ScheduleTableController
import com.example.demo.model.DisplayVersesModel
import com.example.demo.model.ScheduleScope
import com.example.demo.model.datastructure.VerseGroup
import javafx.event.ActionEvent
import javafx.geometry.Side
import javafx.scene.control.TableView
import javafx.scene.input.DragEvent
import javafx.scene.layout.Priority
import tornadofx.controlsfx.detail
import tornadofx.controlsfx.hiddensidepane
import tornadofx.controlsfx.master
import tornadofx.controlsfx.masterdetailpane

class Schedule : Fragment("My View") {
    override val scope = super.scope as ScheduleScope

    private val controller = scope.controller
    private val dragVerseController : DragVerseController by inject(FX.defaultScope)
    private val displayModel : DisplayVersesModel by inject(FX.defaultScope)

    private var tv : TableView<VerseGroup> by singleAssign()

    override val root = masterdetailpane {

         master {
             hiddensidepane {
                 tv = tableview (controller.list) {

                     readonlyColumn("Verse", VerseGroup::verses) {
                         isSortable = false
                         cellFormat {
                             graphic = text {
                                 text = it.joinToString { "${it.translation.abbreviation} ${it.book} ${it.chapter}:${it.verse}" }
                                 wrappingWidthProperty().bind(widthProperty().subtract(20))
                             }
                         }
                         remainingWidth()
                     }
                     bindSelected(displayModel)
                     selectionModel.selectedItemProperty().addListener{_, _, new ->
                        new?.let { controller.setDetail(new) }
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
                     button("Up").setOnAction(::moveSelectedUp)
                     button("Down").setOnAction(::moveSelectedDown)
                     button("Group").setOnAction(::groupSelected)
                     button("Ungroup").setOnAction(::ungroupSelected)
                     button("Delete").setOnAction(::deleteSelected)
                     button("Clear").setOnAction(::clearSchedule)
                 }
                 triggerDistance = 80.toDouble()
             }

         }

        detail {
            listview(controller.detailList) {
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
        showDetailNodeProperty().bind(controller.detailList.sizeProperty.greaterThan(0).and(hoverProperty()))
        detailSide = Side.BOTTOM
    }

    private fun moveSelectedUp(evt : ActionEvent) {
        controller.moveSelectedUp(tv)
        tv.requestFocus()
        evt.consume()
    }

    private fun moveSelectedDown(evt : ActionEvent) {
        controller.moveSelectedDown(tv)
        tv.requestFocus()
        evt.consume()
    }

    private fun groupSelected(evt : ActionEvent) {
        controller.groupSelected(tv)
        tv.requestFocus()
        evt.consume()
    }

    private fun ungroupSelected(evt : ActionEvent) {
        controller.ungroupSelected(tv)
        tv.requestFocus()
        evt.consume()
    }

    private fun deleteSelected(evt : ActionEvent) {
        controller.deleteSelected(tv)
        tv.requestFocus()
        evt.consume()
    }

    private fun clearSchedule(evt : ActionEvent) {
        controller.clear()
        tv.requestFocus()
        evt.consume()
    }

    private fun dragOver(evt: DragEvent) {
        println("Drag Entered")
        dragVerseController.dragOver(evt, tv)
    }

    private fun dragDrop(evt: DragEvent) {
        println("Drag drop")
        dragVerseController.dragDrop(evt, controller.list)
    }
 }
