package com.verseviewer.application.view.editor

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.ProjectionEditorController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.FontModel
import com.verseviewer.application.model.ProjectionModel
import com.verseviewer.application.model.datastructure.VerseGroup
import com.verseviewer.application.model.event.CloseProjection
import com.verseviewer.application.model.event.OpenProjection
import com.verseviewer.application.model.event.SaveProjectionEditorSettings
import com.verseviewer.application.model.scope.ProjectionEditorScope
import com.verseviewer.application.view.projection.Projection
import com.verseviewer.application.view.projection.ScalingPane
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ToggleButton
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import javafx.scene.transform.Scale
import javafx.stage.Screen
import javafx.util.Duration
import org.controlsfx.control.PopOver
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import tornadofx.*
import tornadofx.controlsfx.plusminuslider
import tornadofx.controlsfx.popover
import tornadofx.controlsfx.segmentedbutton
import tornadofx.controlsfx.showPopover


class ProjectionEditor : View() {

    override val scope = super.scope as ProjectionEditorScope

    private val displayVersesModel : DisplayVersesModel by inject()
    private var projectionView : Fragment by singleAssign()
    private val controller : ProjectionEditorController by inject(FX.defaultScope)
    private val fontModel : FontModel by inject()
    private val projectionModel : ProjectionModel by inject()

    private var popOver : PopOver by singleAssign()

    private val numTranslationsProperty = SimpleIntegerProperty(0)
    private val numVersesProperty = SimpleIntegerProperty(0)

    override val root = borderpane {

        projectionModel.screenBoundsProperty.value = Screen.getScreens().first().visualBounds
        projectionView = find<Projection>(mapOf("isCloseable" to false))

        center = ScalingPane(projectionView.root, projectionModel.screenBounds.width, projectionModel.screenBounds.height)

        bottom = anchorpane {
            hbox {
                anchorpaneConstraints {
                    topAnchor = 5.0
                    leftAnchor = 5.0
                }
                paddingAll = 10.0
                label("Translations") {
                    hboxConstraints { marginRight = 10.0 }
                }

                slider(0, 5, 0) {
                    numTranslationsProperty.bind(valueProperty())
                    isSnapToTicks = true
                    isShowTickLabels = true
                    isShowTickMarks = true
                    blockIncrement = 1.0
                    majorTickUnit = 1.0
                    minorTickCount = 0
                    hboxConstraints { marginRight = 30.0 }
                    valueProperty().addListener { _, _, new -> value = new.toInt().toDouble() }
                    setOnMouseReleased {
                        runAsync {
                            controller.getTestVerses(numTranslationsProperty.value, numVersesProperty.value)
                        } ui {
                            displayVersesModel.item = VerseGroup(it)
                        }
                    }
                }

                label("Verses") {
                    hboxConstraints { marginRight = 10.0 }
                }

                slider(0, 15, 0) {
                    numVersesProperty.bind(valueProperty())
                    isSnapToTicks = true
                    isShowTickLabels = true
                    isShowTickMarks = true
                    blockIncrement = 1.0
                    majorTickUnit = 5.0
                    minorTickCount = 4
                    prefWidth = 250.0
                    hboxConstraints { marginRight = 30.0 }
                    valueProperty().addListener { _, _, new -> value = new.toInt().toDouble() }
                    setOnMouseReleased {
                        runAsync {
                            controller.getTestVerses(numTranslationsProperty.value, numVersesProperty.value)
                        } ui {
                            displayVersesModel.item = VerseGroup(it)
                        }
                    }
                }
            }

            vbox {
                alignment = Pos.CENTER
                paddingAll = 10.0
                anchorpaneConstraints {
                    topAnchor = 5.0
                    rightAnchor = 5.0
                }
                togglebutton("Live", selectFirst = false) {
                    vboxConstraints { vGrow = Priority.ALWAYS }
                    selectedProperty().addListener { _, _, new ->
                        if (new) {
                            fire(OpenProjection(scope))
                        } else {
                            fire(CloseProjection(scope))
                        }
                    }
                }
            }
        }

        right = scrollpane {
            form {
                fieldset("1. Text") {
                    field("Alignment") {
                        segmentedbutton {
                            buttons.add(createAlignmentButton(FontAwesome.Glyph.ALIGN_LEFT, TextAlignment.LEFT))
                            buttons.add(createAlignmentButton(FontAwesome.Glyph.ALIGN_CENTER, TextAlignment.CENTER))
                            buttons.add(createAlignmentButton(FontAwesome.Glyph.ALIGN_RIGHT, TextAlignment.RIGHT))
                            buttons.add(createAlignmentButton(FontAwesome.Glyph.ALIGN_JUSTIFY, TextAlignment.JUSTIFY))
                        }
                    }
                }
                fieldset("2. Font") {
                    field("Family") {
                        button {
                            textProperty().bind(fontModel.itemProperty.stringBinding{"${it?.fontFamily}"})
                            popOver = popover {
                                this.title = "Font Picker"
                                find<FontPicker>().root
                            }
                            action {
                                showPopover()
                            }
                        }
                    }
                }
                fieldset("3. Multiple Translations") {
                    field("Orientation") {
                        segmentedbutton {
                            buttons.add(togglebutton { graphic = Styles.fontAwesome.create(FontAwesome.Glyph.BARS)
                                action {
                                    projectionModel.orientation = Orientation.VERTICAL
                                }
                            })
                            buttons.add(togglebutton { graphic = Styles.fontAwesome.create(FontAwesome.Glyph.BARS).apply {
                                style {
                                    rotate = 90.deg
                                }
                                action {
                                    projectionModel.orientation = Orientation.HORIZONTAL
                                }
                            }})
                        }
                    }
                }
            }
        }
    }

    init {
        fontModel.item = scope.savedFontModel.item
        projectionModel.item = scope.savedProjectionModel.item
    }

    override fun onDock() {
        println("ondock PE")
        currentStage?.let { it.setOnCloseRequest {
                popOver.hide(Duration.millis(0.0))
            currentStage!!.widthProperty().onChange { width -> println(width) }
            }
        }

    }

    override fun onUndock() {
        println("undock PE")

        scope.savedProjectionModel.item = projectionModel.item
        scope.savedFontModel.item = fontModel.item
        fire(SaveProjectionEditorSettings())
    }

    private fun createAlignmentButton(glyph : FontAwesome.Glyph, textAlignment: TextAlignment) : ToggleButton {
        return ToggleButton().apply {
            graphic = Styles.fontAwesome.create(glyph)
            action { projectionModel.textAlignment = textAlignment }
        }
    }
}