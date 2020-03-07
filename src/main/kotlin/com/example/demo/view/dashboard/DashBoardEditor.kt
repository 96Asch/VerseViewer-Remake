package com.example.demo.view.dashboard

import com.example.demo.app.Styles
import com.example.demo.controller.DashBoardController
import com.example.demo.controller.DashBoardEditorController
import com.example.demo.model.TilePropertiesModel
import eu.hansolo.tilesfx.Tile
import javafx.scene.Cursor
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFont
import org.controlsfx.glyphfont.GlyphFontRegistry
import tornadofx.*
import tornadofx.controlsfx.segmentedbutton

class DashBoardEditor : View("My View") {

    private val controller : DashBoardEditorController by inject()
    private val dashboardController : DashBoardController by inject()
    private val dashboard = find(DashBoard::class)

    override val root = stackpane {
        borderpane {

            center = dashboard.root

            right = vbox {
                datagrid(controller.componentList) {
                    maxCellsInRow = 1
                    cellWidth = 75.0
                    cellHeight = 100.0
                    maxWidth = 125.0
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
                        enableWhen(controller.dirtyProperty)
                    }
                    button(graphic = glyph.create(FontAwesome.Glyph.REFRESH)) {
                        paddingAll = bWidth
                        enableWhen(controller.dirtyProperty)
                        action {
                            dashboard.root.children.removeIf { it is Tile }
                            runAsyncWithProgress {
                                dashboardController.refreshTiles()
                            } ui {dashboard.addTiles()}
                            controller.dirtyProperty.value = false
                        }
                    }
                    button(graphic = glyph.create(FontAwesome.Glyph.ERASER)).paddingAll = bWidth
                }
            }


            addEventFilter(MouseEvent.MOUSE_PRESSED, ::startDrag)
            addEventFilter(MouseEvent.MOUSE_DRAGGED, ::animateDrag)
            addEventFilter(MouseEvent.MOUSE_RELEASED, ::stopDrag)
            addEventFilter(MouseEvent.MOUSE_RELEASED, ::drop)
        }

        pane {
            this.addClass(Styles.transparent)
            isMouseTransparent = true
        }
    }

    private fun startDrag(evt: MouseEvent) {
        controller.startDrag(evt)
    }

    private fun animateDrag(evt : MouseEvent) {
        controller.animateDrag(evt)
    }

    private fun stopDrag (evt : MouseEvent) {
        controller.stopDrag(evt)
    }

    private fun drop(evt: MouseEvent) {
        controller.drop(evt)
    }
}