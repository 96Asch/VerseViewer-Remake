package com.verseviewer.application.view.dashboard

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.DashBoardController
import com.verseviewer.application.controller.DashBoardEditorController
import com.verseviewer.application.model.event.PlaceInFlightTile
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

            right = tabpane {
                tab("Main") {
                    vbox {
                        paddingAll = 10.0
                        datagrid(controller.componentList) {
                            maxCellsInRow = 1
                            cellWidth = 100.0
                            cellHeight = 150.0
                            maxWidth = 170.0
                            paddingAll = 10.0
                            cellFormat {
                                graphic = it.apply { addClass(Styles.highlightTile) }
                            }

                            vboxConstraints {
                                vGrow = Priority.ALWAYS
                            }
                        }

                        hbox {
                            val glyph = GlyphFontRegistry.font("FontAwesome")
                            val bWidth = 10.0
                            button(graphic = glyph.create(FontAwesome.Glyph.SAVE)) {
                                paddingAll = bWidth
                                enableWhen(controller.dirtyProperty.and(controller.requiredComponentsUsedProperty))
                                action { saveGrid() }
                            }
                            button(graphic = glyph.create(FontAwesome.Glyph.REFRESH)) {
                                paddingAll = bWidth
                                enableWhen(controller.dirtyProperty)
                                action { refreshGrid() }
                            }
                            button(graphic = glyph.create(FontAwesome.Glyph.ERASER)) {
                                paddingAll = bWidth
                                action { eraseGrid() }
                            }
                        }
                    }
                }

                tab("Preferences") {
                    form {
                        fieldset("Set") {

                        }
                    }
                }

                tabs.sizeProperty.onChange {
                    val tabWidth = width / tabs.size
                    tabMinWidth = tabWidth
                    tabMaxWidth = tabWidth
                }
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            }

            addEventFilter(MouseEvent.MOUSE_PRESSED, ::startDrag)
            addEventFilter(MouseEvent.MOUSE_DRAGGED, ::animateDrag)
            addEventFilter(MouseEvent.MOUSE_RELEASED, ::stopDrag)
            addEventFilter(MouseEvent.MOUSE_RELEASED, ::drop)
        }

        this += dragPane
    }

    private fun eraseGrid() {
        dashboardController.clearTiles()
        dashboard.refreshTiles()
        controller.dirty = true
        controller.requiredComponentsUsed = false
    }

    private fun refreshGrid() {
        dashboard.refreshTiles()
        dashboardController.refreshTiles()
        controller.dirty = false
        controller.requiredComponentsUsed = true
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