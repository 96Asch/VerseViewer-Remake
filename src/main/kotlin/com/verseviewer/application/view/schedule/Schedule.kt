package com.verseviewer.application.view.schedule

import com.verseviewer.application.controller.DragVerseController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.datastructure.VerseGroup
import com.verseviewer.application.model.event.DeselectVerses
import com.verseviewer.application.model.scope.ScheduleScope
import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.scene.control.ContextMenu
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.util.Duration
import tornadofx.*
import java.util.*


class Schedule : Fragment("My View") {
    override val scope = super.scope as ScheduleScope

    private val controller = scope.controller
    private val dragVerseController : DragVerseController by inject(FX.defaultScope)
    private val displayModel : DisplayVersesModel by inject(FX.defaultScope)

    private val armedProperty = SimpleBooleanProperty()
    private var tv : TableView<VerseGroup> by singleAssign()

    override val root = anchorpane {
        tv = tableview(controller.list) {
            readonlyColumn("Verse", VerseGroup::verses) {
                isSortable = false
                cellFormat {
                    graphic = text {
                        text = it.joinToString { "${it.translation.abbreviation} ${it.book} ${it.chapter}:${it.verse}" }
                        wrappingWidthProperty().bind(widthProperty().subtract(20))
                    }
                }
                setRowFactory { rowFactory(this@tableview) }
                remainingWidth()
            }

            anchorpaneConstraints {
                rightAnchor = 0.0
                leftAnchor = 0.0
                topAnchor = 0.0
                bottomAnchor = 0.0
            }


            selectionModel.selectedItemProperty().onChange {
                it?.let {
                    controller.setDetail(it)
                    if (armedProperty.value) {
                        displayModel.item = it
                    }
                    fire(DeselectVerses(this.toString()))
                }
            }

            subscribe<DeselectVerses> {
                if (it.id != this@tableview.toString()) {
                    selectionModel.clearSelection()
                }
            }

            setOnDragDropped(::dragDrop)
            setOnDragOver(::dragOver)
            multiSelect(true)
            smartResize()
            hboxConstraints {
                hgrow = Priority.ALWAYS
            }
            val contextMenu = ContextMenu().apply {
                item("1")
                item("2")
                item("3")
            }

            setOnContextMenuRequested {
                if (selectionModel.selectedItems.isNotEmpty()) {
                    println("Context for ${selectionModel.selectedItems}")
                    contextMenu.show(this@anchorpane, it.screenX, it.screenY)
                    val yIni = it.screenX - 100.0
                    val yEnd: Double = contextMenu.x
                    contextMenu.x = yIni


                    val yProperty: DoubleProperty = SimpleDoubleProperty(yIni)
                    yProperty.addListener { _, _, n1: Number -> contextMenu.x = n1.toDouble() }

                    val timeIn = Timeline()
                    timeIn.keyFrames.add(
                            KeyFrame(Duration.millis(200.0),
                                    KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)))
                    timeIn.play()
                }
            }
        }

        togglebutton(selectFirst = false) {
            text = "Arm"
            anchorpaneConstraints { rightAnchor = 1.0 }
            armedProperty.bind(selectedProperty())
        }
    }

    private fun rowFactory(tv: TableView<VerseGroup>) = TableRow<VerseGroup>().apply {
        setOnDragDetected { controller.dragDetected(it, this)}
        setOnDragOver {
            controller.dragOver(it)
            dragOver(it)
        }
        setOnDragEntered { controller.dragEntered(it, this) }
        setOnDragExited { controller.dragExited(it, this) }
        setOnDragDropped { controller.dragDropped(it, this)}
        setOnDragDone { controller.dragDone(it) }
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
        dragVerseController.dragOver(evt, tv)
    }

    private fun dragDrop(evt: DragEvent) {
        dragVerseController.dragDrop(evt, controller.list)
    }
 }
