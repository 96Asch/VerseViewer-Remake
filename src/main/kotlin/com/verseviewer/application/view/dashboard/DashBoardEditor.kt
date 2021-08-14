package com.verseviewer.application.view.dashboard

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.DashBoardController
import com.verseviewer.application.controller.DashBoardEditorController
import com.verseviewer.application.model.event.PlaceInFlightTile
import com.verseviewer.application.model.event.ResetTiles
import javafx.geometry.Pos
import javafx.scene.control.TabPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import tornadofx.*

class DashBoardEditor : View() {

    private val controller : DashBoardEditorController by inject()
    private val dashboardController : DashBoardController by inject()
    private val dashboard = find<DashBoard>(mapOf("inEditor" to true))

    private val dragPane : Pane by lazy {
        pane {
            this.addClass(Styles.transparent)
            isMouseTransparent = true
        }
    }

    override val root = stackpane {
        borderpane {

            center = dashboard.root

            right = vbox {
                tabpane {
                    tab("Main") {
                        datagrid(controller.componentList) {
                            maxCellsInRow = 1
                            cellWidth = 150.0
                            cellHeight = 130.0
                            maxWidth = 190.0
                            cellFormat {
                                graphic = it.apply { addClass(Styles.highlightTile) }
                            }

                            vboxConstraints {
                                vGrow = Priority.ALWAYS
                            }
                            spacing = 10.0
                        }
                    }

                    tab("Preferences") {
                        form {
                            fieldset("Set") {

                            }
                        }
                    }
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                }

                hbox {
                    val bWidth = 10.0
                    button(graphic=Styles.fontAwesome.create(FontAwesome.Glyph.SAVE)) {
                        paddingAll = bWidth
                        enableWhen(controller.dirtyProperty.and(controller.requiredComponentsUsedProperty))
                        action { saveGrid() }
                    }
                    button(graphic=Styles.fontAwesome.create(FontAwesome.Glyph.REFRESH)) {
                        paddingAll = bWidth
                        enableWhen(controller.dirtyProperty)
                        action { refreshGrid() }
                    }
                    button(graphic=Styles.fontAwesome.create(FontAwesome.Glyph.ERASER)) {
                        paddingAll = bWidth
                        action { eraseGrid() }
                    }
                    alignment = Pos.CENTER
                    spacing = 5.0
                    paddingAll = 5.0
                }
            }
        }

        this += dragPane
        addEventFilter(MouseEvent.MOUSE_PRESSED, ::startDrag)
        addEventFilter(MouseEvent.MOUSE_DRAGGED, ::animateDrag)
        addEventFilter(MouseEvent.MOUSE_RELEASED, ::stopDrag)
        addEventFilter(MouseEvent.MOUSE_RELEASED, ::drop)
    }

    private fun eraseGrid() {
        dashboardController.clearTiles()
        controller.dirty = true
        controller.requiredComponentsUsed = false
        fire(ResetTiles())
    }

    private fun refreshGrid() {
        dashboardController.initGrid()
        controller.dirty = false
        controller.requiredComponentsUsed = true
        fire(ResetTiles())
    }

    private fun saveGrid() {
        controller.saveDashboard()
    }

    private fun startDrag(evt: MouseEvent) {
        val mousePt = root.sceneToLocal(evt.sceneX, evt.sceneY)
        controller.startDrag(evt, mousePt)
    }

    private fun animateDrag(evt : MouseEvent) {
        val mousePt = root.sceneToLocal(evt.sceneX, evt.sceneY)
        controller.animateDrag(evt, mousePt)
    }

    private fun stopDrag (evt : MouseEvent) {
        controller.stopDrag()
    }

    private fun drop(evt: MouseEvent) {
        val mousePt = root.sceneToLocal(evt.sceneX, evt.sceneY)
        controller.drop(evt, mousePt)
    }

    init {
        subscribe<PlaceInFlightTile> {
            dragPane.add(it.tile)
        }
    }
}