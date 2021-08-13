package com.verseviewer.application.view.main

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.MainViewController
import com.verseviewer.application.model.*
import com.verseviewer.application.model.VerseGroup
import com.verseviewer.application.model.event.*
import com.verseviewer.application.model.scope.ProjectionEditorScope
import com.verseviewer.application.util.showForSeconds
import com.verseviewer.application.view.dashboard.DashBoard
import com.verseviewer.application.view.dashboard.DashBoardEditor
import com.verseviewer.application.view.editor.ProjectionPreferenceEditor
import com.verseviewer.application.view.projection.Projection
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.stage.FileChooser
import javafx.stage.Screen
import javafx.stage.StageStyle
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.hiddensidepane
import tornadofx.controlsfx.notificationPane
import tornadofx.controlsfx.pinSide

class MainView : View() {

    private val snapshotModel : SnapshotModel by inject()
    private val projectionModel : ProjectionModel by inject()
    private val displayModel : VerseGroupModel by inject()

    private val pEditorScope = ProjectionEditorScope()
    private val controller : MainViewController by inject()

    private val projectionView : Projection by lazy { find() }

    override val root = notificationPane {

        content = hiddensidepane {

            left = hbox {
                vbox {
                    listmenu {
                        item(text = "Dashboard") {
                            whenSelected {
                                fire(ChangeScene(find<DashBoard>().root))
                            }
                        }
                        item("Projection Settings") {
                            whenSelected {
                                pEditorScope.savedSnapshotModel.item = snapshotModel.item
                                pEditorScope.savedProjectionModel.item = projectionModel.item
                                fire(ChangeScene(find<ProjectionPreferenceEditor>(pEditorScope).root))
                            }
                        }
                        item("Dashboard Editor") {
                            whenSelected {
                                fire(ChangeScene(find<DashBoardEditor>(Scope()).root))
                            }
                        }
                        item("Snapshots") {
                            whenSelected {

                            }
                        }
                        orientation = Orientation.VERTICAL
                        iconPosition = Side.LEFT
                    }
                    paddingAll = 4
                    addClass(Styles.slideMenu)
                }

                button {
                    graphic = Styles.fontAwesome.create(FontAwesome.Glyph.ARROW_LEFT)
                    action { pinnedSide = null }
                    addClass(Styles.transparentButton)
                }
            }

            content = borderpane {

                projectionModel.item = ProjectionData()
                displayModel.item = VerseGroup(listOf())

                top = anchorpane {
                    button {
                        graphic = Styles.fontAwesome.create(FontAwesome.Glyph.ALIGN_JUSTIFY)
                        addClass(Styles.transparentButton)
                        action { pinSide = Side.LEFT }
                        anchorpaneConstraints {
                            leftAnchor = 1.0
                        }
                        enableWhen(controller.snapshotLoadedProperty)
                    }

                    hbox {
                        togglebutton(selectFirst = false) {
                            prefHeightProperty().bind(this@anchorpane.heightProperty())
                            action { openProjection(isSelected) }
                            enableWhen { displayModel.itemProperty.selectBoolean { it.verses.sizeProperty.ge(0) } }
                            textProperty().bind(snapshotModel.displayIndexProperty.stringBinding { "Display $it" })
                            addClass(Styles.liveButton)
                            enableWhen(controller.snapshotLoadedProperty)
                        }
                        anchorpaneConstraints {
                            rightAnchor = 1.0
                        }
                    }
                }

                center = vbox {
                    label("PLACEHOLDER") { }

                    button(controller.databasePathProperty) {
                        addClass(Styles.transparentButton)
                        action {
                            val files = chooseFile(
                                "Select the database",
                                controller.exts.toTypedArray(),
                                null,
                                FileChooserMode.Single
                            )
                            if (files.isNotEmpty()) {
                                controller.setDBPath(files.first().absolutePath)
                                preferences(controller.prefName) {
                                    put("db_location", controller.databasePath)
                                }
                            }
                        }
                    }

                    button(graphic = Styles.fontAwesome.create(FontAwesome.Glyph.PLAY)) {
                        action {
                            controller.loadSnapshot(controller.snapshotIndex)?.let { snapshot ->
                                snapshotModel.item = snapshot
                                fire(ChangeScene(find<DashBoard>().root))
                            }
                        }
                    }
                    alignment = Pos.CENTER
                }

                subscribe<ChangeScene> {
                    center.replaceWith(it.node)
                    pinnedSide = null
                }
            }
            triggerDistance = 0.0
            setOnMouseClicked {
                pinnedSide = null
                it.consume()
            }
        }

        subscribe<SendGlobalNotification> {
            showForSeconds(it.type, it.message, it.duration)
        }
    }

    override fun onDock() {
        currentStage?.titleProperty()?.unbind()
        currentStage?.title = "VerseViewer 2.0"
        currentStage?.setOnCloseRequest {
            fire(CloseProjection(scope))
        }
        currentStage?.isAlwaysOnTop = true
    }

    override fun onUndock() {
        fire(CloseProjection(scope))
    }

    private fun openProjection(isSelected : Boolean) {
        if (isSelected) {
            // Sets the current screen to the saved screen, else sets it to the current monitor.
            projectionModel.screenBounds = Screen.getScreens()
                    .getOrElse(snapshotModel.displayIndex.toInt()) { Screen.getScreens().first() }
                    .visualBounds

            projectionView.openWindow(StageStyle.TRANSPARENT, escapeClosesWindow = false, owner = null)
            fire(OpenProjection(scope))
        }
        else {
            fire(CloseProjection(scope))
        }
        projectionModel.isLive = isSelected
    }

    init {
        subscribe<LoadProjectionEditorSettings> {
            snapshotModel.item = pEditorScope.savedSnapshotModel.item
            projectionModel.item = pEditorScope.savedProjectionModel.item
        }
        preferences(controller.prefName) {
            controller.setDBPath(get("db_location", "NONE"))
            controller.snapshotIndex = getInt("snap_index", 0)
        }
    }

}