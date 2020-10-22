package com.verseviewer.application.view.schedule

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.DragVerseController
import com.verseviewer.application.model.VerseGroupModel
import com.verseviewer.application.model.VerseGroup
import com.verseviewer.application.model.event.DeselectVerses
import com.verseviewer.application.model.event.SendScheduleNotification
import com.verseviewer.application.model.scope.ScheduleScope
import com.verseviewer.application.util.showForSeconds
import javafx.animation.*
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.DragEvent
import javafx.scene.layout.Priority
import javafx.util.Duration
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.notificationPane


class Schedule : Fragment("My View") {
    override val scope = super.scope as ScheduleScope

    private val controller = scope.controller
    private val dragVerseController : DragVerseController by inject(FX.defaultScope)
    private val displayModel : VerseGroupModel by inject(FX.defaultScope)

    private val contextAnimation = ParallelTransition()
    private val armedProperty = SimpleBooleanProperty()
    private var tv : TableView<VerseGroup> by singleAssign()

    private val groupItem = MenuItem("1").apply {
        graphic = Styles.fontAwesome.create(FontAwesome.Glyph.LINK)
        setOnAction {
            controller.groupSelected(tv)
            it.consume()
        }
    }

    private val ungroupItem = MenuItem("2").apply {
        graphic = Styles.fontAwesome.create(FontAwesome.Glyph.UNLINK)
        setOnAction {
            controller.ungroupSelected(tv)
            it.consume()
        }
    }

    private val deleteItem = MenuItem("3").apply {
        graphic = Styles.fontAwesome.create(FontAwesome.Glyph.TRASH)
        setOnAction {
            controller.deleteSelected(tv)
            it.consume()
        }
    }

    @ExperimentalStdlibApi
    override val root = notificationPane {
        content = hbox {
            tv = tableview(controller.list) {
                readonlyColumn("Verse", VerseGroup::verses) {
                    isSortable = false
                    cellFormat {
                        graphic = text {
                            text = it.joinToString { "${it.translation.abbreviation} ${it.book} ${it.chapter}:${it.verse}" }
                            wrappingWidthProperty().bind(widthProperty().subtract(20))
                        }
                    }
                    setRowFactory { buildScheduleRow() }
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

            vbox {
                paddingLeft = 5.0
                spacing = 5.0

                togglebutton(selectFirst = false) {
                    graphic = Styles.fontAwesome.create(FontAwesome.Glyph.CIRCLE)
                    armedProperty.bind(selectedProperty())
                    action {
                        if (isSelected && tv.selectedItem != null)
                            displayModel.item = tv.selectedItem
                    }
                }

                button {
                    graphic = Styles.fontAwesome.create(FontAwesome.Glyph.SAVE)
                    action { saveSchedule() }
                    enableWhen { controller.list.sizeProperty.ge(1) }
                }
                button {
                    graphic = Styles.fontAwesome.create(FontAwesome.Glyph.FILE_TEXT)
                    action { loadSchedule() }
                }
            }
        }

        isShowFromTop = false
        subscribe<SendScheduleNotification> {
            showForSeconds(it.type, it.message, it.duration)
        }
    }

    @ExperimentalStdlibApi
    private fun buildScheduleRow() = TableRow<VerseGroup>().apply {
        setOnDragDetected { controller.dragDetected(it, this)}
        setOnDragOver { controller.dragOver(it) }
        setOnDragEntered { controller.dragEntered(it, this) }
        setOnDragExited { controller.dragExited(it, this) }
        setOnDragDropped { controller.dragDropped(it, this)}
        setOnDragDone { controller.dragDone(it) }


        val cm = ContextMenu().apply {
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
                buildMenuItems(cm, tableView.selectionModel.selectedItems)
                cm.show(this, it.screenX, it.screenY)
                cm.opacity = 0.0
                addTranslateXAnimation(cm, it.screenX)
                contextAnimation.playFromStart()
            }
        }
    }

    private fun saveSchedule() {
        val locations = chooseFile("Save Schedule", controller.scheduleExt, mode = FileChooserMode.Save)
        if (locations.isNotEmpty())
            controller.saveSchedule(locations.first().toPath())
    }

    private fun loadSchedule() {
        val locations = chooseFile("Load Schedule", controller.scheduleExt)
        if (locations.isNotEmpty())
            controller.loadSchedule(locations.first().toPath())
    }

    private fun buildMenuItems(cm : ContextMenu, selected : List<VerseGroup>) {
        cm.items.clear()
        if (selected.size > 1)
            cm.items.add(groupItem)
        if (selected.any { vg -> vg.verses.size > 1 })
            cm.items.add(ungroupItem)
        cm.items.add(deleteItem)
    }

    @ExperimentalStdlibApi
    private fun addTranslateXAnimation(cm : ContextMenu, posX : Double) {
        val translateXProperty = SimpleDoubleProperty(posX - 100.0)
        val translateX = Timeline(
                KeyFrame(Duration.millis(200.0),
                        KeyValue(translateXProperty, posX))
        )
        translateXProperty.onChange { x -> cm.x = x }
        contextAnimation.children.removeLastOrNull()
        contextAnimation.children.add(translateX)
    }

    private fun dragOver(evt: DragEvent) {
        dragVerseController.dragOver(evt, tv)
    }

    private fun dragDrop(evt: DragEvent) {
        dragVerseController.dragDrop(evt, controller.list)
    }
 }
