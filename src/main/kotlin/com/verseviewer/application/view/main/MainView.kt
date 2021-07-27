package com.verseviewer.application.view.main

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.MainViewController
import com.verseviewer.application.model.*
import com.verseviewer.application.model.VerseGroup
import com.verseviewer.application.model.event.CloseProjection
import com.verseviewer.application.model.event.OpenProjection
import com.verseviewer.application.model.event.LoadProjectionEditorSettings
import com.verseviewer.application.model.scope.ProjectionEditorScope
import com.verseviewer.application.view.dashboard.DashBoard
import com.verseviewer.application.view.dashboard.DashBoardEditor
import com.verseviewer.application.view.editor.ProjectionPreferenceEditor
import com.verseviewer.application.view.projection.Projection
import javafx.geometry.Orientation
import javafx.geometry.Side
import javafx.scene.Node
import javafx.stage.Screen
import javafx.stage.StageStyle
import tornadofx.*

class MainView : View() {

    private val preferenceModel : PreferenceModel by inject()
    private val projectionModel : ProjectionModel by inject()
    private val displayModel : VerseGroupModel by inject()
    private val userModel : UserModel by inject()

    private val controller : MainViewController by inject()

    private val dashboard : DashBoard by inject()

    private val projectionEditorScope = ProjectionEditorScope()

    override val root = borderpane {

        projectionModel.item = ProjectionData()
        displayModel.item = VerseGroup(listOf())

        top = anchorpane {
            listmenu {
                item("Dashboard") {
                    activeItem = this
                    whenSelected { setupDashboardView(center) }
                }
                item("Projection Settings") {
                    whenSelected { setupProjectionEditorView(center) }
                }
                item("Dashboard Editor") {
                    whenSelected { setupDashboardEditorView(center) }
                }
                item("Snapshots") {

                }

                anchorpaneConstraints {
                    leftAnchor = 0.0
                }
                orientation = Orientation.HORIZONTAL
                iconPosition = Side.TOP
            }

            hbox {
                togglebutton( selectFirst = false) {
                    prefHeightProperty().bind(this@anchorpane.heightProperty())
                    action { openProjection(isSelected) }
                    enableWhen { displayModel.itemProperty.selectBoolean { it.verses.sizeProperty.ge(0) }}
                    textProperty().bind(preferenceModel.displayIndexProperty.stringBinding { "Display $it" })
                    addClass(Styles.liveButton)
                }
                anchorpaneConstraints {
                    rightAnchor = 1.0
                }
            }
        }
        center<DashBoard>()
    }

    override fun onDock() {
        currentStage?.titleProperty()?.unbind()
        currentStage?.title = "VerseViewer 2.0 - ${userModel.name.value}"
        currentStage?.setOnCloseRequest {
            fire(CloseProjection(scope))
        }
    }

    override fun onUndock() {
        fire(CloseProjection(scope))
    }

    private fun openProjection(isSelected : Boolean) {
        if (isSelected) {
            projectionModel.screenBounds = Screen.getScreens()
                    .getOrElse(preferenceModel.displayIndex.toInt()) { Screen.getScreens().first() }.visualBounds
            println(projectionModel.screenBounds)
            find<Projection>().openWindow(StageStyle.TRANSPARENT, escapeClosesWindow = false, owner = null)
            fire(OpenProjection(scope))
        }
        else {
            fire(CloseProjection(scope))
        }
        projectionModel.isLive = isSelected
    }

    private fun setupDashboardView(node : Node) {
        dashboard.root.children.clear()
        node.replaceWith(dashboard.root)
    }

    private fun setupProjectionEditorView(node : Node) {
        projectionEditorScope.savedPreferenceModel.item = preferenceModel.item

        projectionEditorScope.savedProjectionModel.item = projectionModel.item
        node.replaceWith(find<ProjectionPreferenceEditor>(projectionEditorScope).root)
    }

    private fun setupDashboardEditorView(node : Node) {
        node.replaceWith(find<DashBoardEditor>(Scope()).root)
    }

    init {
        subscribe<LoadProjectionEditorSettings> {
            preferenceModel.item = projectionEditorScope.savedPreferenceModel.item
            projectionModel.item = projectionEditorScope.savedProjectionModel.item
        }
    }

}