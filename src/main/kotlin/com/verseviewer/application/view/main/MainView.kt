package com.verseviewer.application.view.main

import com.verseviewer.application.app.Styles
import com.verseviewer.application.model.*
import com.verseviewer.application.model.event.CloseProjection
import com.verseviewer.application.model.event.LoadDashBoardEditorSettings
import com.verseviewer.application.model.event.OpenProjection
import com.verseviewer.application.model.event.LoadProjectionEditorSettings
import com.verseviewer.application.model.scope.ProjectionEditorScope
import com.verseviewer.application.view.dashboard.DashBoard
import com.verseviewer.application.view.dashboard.DashBoardEditor
import com.verseviewer.application.view.editor.ProjectionEditor
import com.verseviewer.application.view.projection.Projection
import javafx.geometry.Orientation
import javafx.geometry.Side
import javafx.scene.Node
import javafx.stage.StageStyle
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class MainView : View() {

    private val fontModel : FontModel by inject()
    private val projectionModel : ProjectionModel by inject()
    private val displayModel : DisplayVersesModel by inject()
    private val textStyleModel : TextStyleModel by inject()

    private val dashboardView : DashBoard by inject()
    private var projectionView : Projection by singleAssign()
    private val projectionEditorScope = ProjectionEditorScope()

    private val dashBoardEditorView : DashBoardEditor by inject(Scope())

    override val root = borderpane {

        top = anchorpane {
            listmenu {
                item("Dashboard") {
                    activeItem = this
                    whenSelected { center.replaceWith(find<DashBoard>().root) }
                }
                item("Projection Settings") {
                    whenSelected { setupProjectionEditorView(center) }
                }
                item("Dashboard Editor") {
                    whenSelected { center.replaceWith(dashBoardEditorView.root) }
                }

                anchorpaneConstraints {
                    leftAnchor = 0.0
                }
                orientation = Orientation.HORIZONTAL
                iconPosition = Side.TOP
            }

            hbox {
                togglebutton("Live", selectFirst = false) {
                    prefHeightProperty().bind(this@anchorpane.heightProperty())
                    action {
                        if (isSelected) {
                            projectionView.openWindow(StageStyle.TRANSPARENT, escapeClosesWindow = false, owner = null)
                            fire(OpenProjection(scope))
                        }
                        else {
                            fire(CloseProjection(scope))
                        }
                        projectionModel.isLive = isSelected
                    }
                }
                label {
                    graphic = Styles.fontAwesome.create(FontAwesome.Glyph.ARROW_CIRCLE_O_RIGHT)
                    prefHeightProperty().bind(this@anchorpane.heightProperty())
                }
                label {
                    textProperty().bind(projectionModel.displayIndexProperty.stringBinding {"Display $it"})
                    prefHeightProperty().bind(this@anchorpane.heightProperty())
                }
                anchorpaneConstraints {
                    rightAnchor = 1.0
                }
            }
        }
        center<DashBoard>()
    }

    private fun setupProjectionEditorView(node : Node) {
        projectionEditorScope.savedTextStyleModel.item = textStyleModel.item
        projectionEditorScope.savedFontModel.item = fontModel.item
        projectionEditorScope.savedProjectionModel.item = projectionModel.item
        node.replaceWith(find<ProjectionEditor>(projectionEditorScope).root)
    }

    init {
        fontModel.item = FontData(50.0, "Arial Black")
        projectionModel.item = ProjectionData()
        textStyleModel.item = TextStyle()
        projectionView = find(mapOf("isCloseable" to true))
        subscribe<LoadProjectionEditorSettings> {
            println("Saved settings")
            fontModel.item = projectionEditorScope.savedFontModel.item
            projectionModel.item = projectionEditorScope.savedProjectionModel.item
            textStyleModel.item = projectionEditorScope.savedTextStyleModel.item
        }
    }

}