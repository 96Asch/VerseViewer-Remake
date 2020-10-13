package com.verseviewer.application.view.schedule

import com.verseviewer.application.controller.DragVerseController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.datastructure.VerseGroup
import com.verseviewer.application.model.event.DeselectVerses
import com.verseviewer.application.model.scope.ScheduleScope
import javafx.animation.*
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
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

    private val contextAnimation = ParallelTransition()
    private val armedProperty = SimpleBooleanProperty()
    private var tv : TableView<VerseGroup> by singleAssign()

    @ExperimentalStdlibApi
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
        }

        hbox {
            spacing = 5.0
            button("Save") {

            }
            button("Load") {

            }
            togglebutton(selectFirst = false) {
                text = "Arm"
                armedProperty.bind(selectedProperty())
            }
            anchorpaneConstraints { rightAnchor = 0.0 }

        }


    }

    @ExperimentalStdlibApi
    private fun rowFactory(tv: TableView<VerseGroup>) = TableRow<VerseGroup>().apply {
        setOnDragDetected { controller.dragDetected(it, this)}
        setOnDragOver { controller.dragOver(it) }
        setOnDragEntered { controller.dragEntered(it, this) }
        setOnDragExited { controller.dragExited(it, this) }
        setOnDragDropped { controller.dragDropped(it, this)}
        setOnDragDone { controller.dragDone(it) }

        val groupItem = MenuItem("Group").apply {
            setOnAction {
                controller.groupSelected(tableView)
                it.consume()
            }
        }

        val ungroupItem = MenuItem("Ungroup").apply {
            setOnAction {
                controller.ungroupSelected(tableView)
                it.consume()
            }
        }

        val deleteItem = MenuItem("Delete").apply {
            setOnAction {
                controller.deleteSelected(tableView)
                it.consume()
            }
        }

        val contextMenu = ContextMenu().apply {
            isAutoHide = true

            val fade = Timeline(
                    KeyFrame(Duration.millis(175.0),
                            KeyValue(opacityProperty(), 1.0))
            )
            contextAnimation.apply {
                children.add(fade)
            }
        }

        setOnContextMenuRequested {
            if (this.item != null) {
                val selected = tableView.selectionModel.selectedItems
                contextMenu.items.clear()
                if (selected.size > 1) {
                    contextMenu.items.add(groupItem)
                }
                if (selected.any { vg -> vg.verses.size > 1 }) {
                    contextMenu.items.add(ungroupItem)
                }
                contextMenu.items.add(deleteItem)
                contextMenu.show(this, it.screenX, it.screenY)
                contextMenu.opacity = 0.0
                val translateXProperty = SimpleDoubleProperty(it.screenX - 100.0)
                val translateX = Timeline(
                        KeyFrame(Duration.millis(200.0),
                                KeyValue(translateXProperty, it.screenX))
                )
                translateXProperty.onChange { x -> contextMenu.x = x }

                contextAnimation.children.removeLastOrNull()
                contextAnimation.children.add(translateX)
                contextAnimation.playFromStart()
            }
        }
    }

    private fun dragOver(evt: DragEvent) {
        dragVerseController.dragOver(evt, tv)
    }

    private fun dragDrop(evt: DragEvent) {
        dragVerseController.dragDrop(evt, controller.list)
    }
 }
