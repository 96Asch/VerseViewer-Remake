package com.verseviewer.application.view.dashboard

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.DashBoardController
import com.verseviewer.application.controller.DashBoardEditorController
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import tornadofx.*

class DashBoardEditor : View("My View") {

    private val controller : DashBoardEditorController by inject()
    private val dashboardController : DashBoardController by inject()
    private val dashboard = find(DashBoard::class)

    override val root = stackpane {
        borderpane {

            center = dashboard.root


            right = vbox {
                paddingAll = 10.0
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
                        action { saveGrid() }
                    }
                    button(graphic = glyph.create(FontAwesome.Glyph.REFRESH)) {
                        paddingAll = bWidth
                        enableWhen(controller.dirtyProperty)
                        action { refreshGrid() }
                    }
                    button(graphic = glyph.create(FontAwesome.Glyph.ERASER)) {
                        paddingAll = bWidth
                        action { eraseGrid()}
                    }
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

    private fun eraseGrid() {
        dashboardController.clearTiles()
        dashboard.refreshTiles()
        controller.dirty = true
    }

    private fun refreshGrid() {
        dashboardController.refreshTiles()
        dashboard.refreshTiles()
        controller.dirty = false
    }

    private fun saveGrid() {
        controller.updateGridToDB()
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