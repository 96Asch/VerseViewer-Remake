package com.verseviewer.application.view.editor

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.ProjectionEditorController
import com.verseviewer.application.model.*
import com.verseviewer.application.model.VerseGroup
import com.verseviewer.application.model.event.*
import com.verseviewer.application.model.scope.ProjectionEditorScope
import com.verseviewer.application.view.projection.Projection
import com.verseviewer.application.view.projection.scalingpane
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.control.ToggleButton
import javafx.scene.text.TextAlignment
import javafx.stage.Screen
import javafx.util.Duration
import org.controlsfx.control.PopOver
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.popover
import tornadofx.controlsfx.segmentedbutton
import tornadofx.controlsfx.showPopover


class ProjectionPreferenceEditor : View() {

    override val scope = super.scope as ProjectionEditorScope

    private var projectionView : Fragment by singleAssign()

    private val controller : ProjectionEditorController by inject(FX.defaultScope)

    private val verseGroupModel : VerseGroupModel by inject()
    private val projectionModel : ProjectionModel by inject()
    private val snapshotModel : SnapshotModel by inject()

    private var fontPicker : PopOver by singleAssign()


    override val root = borderpane {

        projectionModel.item = scope.savedProjectionModel.item
        snapshotModel.item = scope.savedSnapshotModel.item

        projectionModel.screenBoundsProperty.value = Screen.getScreens().first().visualBounds

        projectionView = find<Projection>(mapOf("isCloseable" to false))

        center = scalingpane(projectionView.root, projectionModel.screenBounds.width, projectionModel.screenBounds.height)

        right = scrollpane {
            form {
                fieldset("1. Secondary Screen") {
                    combobox(values = controller.screenList) {
                        selectionModel.select(snapshotModel.displayIndex.toInt())
                        selectionModel.selectedItemProperty().onChange {
                            it?.let { snapshotModel.displayIndex = it.index }
                        }
                        cellFormat {
                            text =
                                "Display ${it.index} - [${it.screen.visualBounds.width}x${it.screen.visualBounds.height}]"
                        }
                    }
                }
                fieldset("2. Text") {
                    field("Alignment") {
                        segmentedbutton {
                            subscribe<InitTextAlignmentButton> {
                                buttons.filterIsInstance<ToggleButton>().forEach {
                                    if (it.properties.containsKey("alignment")) {
                                        it.isSelected =
                                            it.properties["alignment"] as TextAlignment == snapshotModel.textAlignment
                                    }
                                }
                            }
                            buttons.add(createAlignmentButton(FontAwesome.Glyph.ALIGN_LEFT, TextAlignment.LEFT))
                            buttons.add(createAlignmentButton(FontAwesome.Glyph.ALIGN_CENTER, TextAlignment.CENTER))
                            buttons.add(createAlignmentButton(FontAwesome.Glyph.ALIGN_RIGHT, TextAlignment.RIGHT))
                            buttons.add(createAlignmentButton(FontAwesome.Glyph.ALIGN_JUSTIFY, TextAlignment.JUSTIFY))
                        }
                    }
                }
                fieldset("3. Font") {
                    button {
                        textProperty().bind(stringBinding(
                            snapshotModel.familyProperty,
                            snapshotModel.sizeProperty,
                            snapshotModel.weightProperty,
                            snapshotModel.postureProperty
                        ) {
                            "${snapshotModel.family}\n ${snapshotModel.size} [${snapshotModel.weight}-${snapshotModel.posture}]"
                        })
                        fontPicker = popover {
                            this.title = "Font Picker"
                            find<FontPicker>().root
                        }
                        action {
                            showPopover()
                        }
                    }
                }

                fieldset("4. Text Styling") {
                    field("Fill Color") {
                        colorpicker { valueProperty().bindBidirectional(snapshotModel.fillProperty) }
                    }
                    field("Border Color") {
                        colorpicker { valueProperty().bindBidirectional(snapshotModel.strokeProperty) }
                    }
                    field("Border Width") {
                        vbox {
                            val bwProperty = SimpleDoubleProperty()
                            slider(0, 5, 1) {
                                isSnapToTicks = true
                                isShowTickLabels = true
                                isShowTickMarks = true
                                blockIncrement = 1.0
                                majorTickUnit = 5.0

                                valueProperty().bindBidirectional(snapshotModel.strokeWidthProperty)
                                bwProperty.bind(valueProperty())
                            }
                            label {
                                textProperty().bind(stringBinding(bwProperty) {
                                    "${bwProperty.value.format(3)}"
                                })
                            }
                            alignment = Pos.CENTER
                        }
                    }
                }
                fieldset("5. Multiple Translations") {
                    field("Orientation") {
                        segmentedbutton {
                            subscribe<InitBoxLayoutButton> {
                                buttons.filterIsInstance<ToggleButton>().forEach {
                                    if (it.properties.containsKey("orientation")) {
                                        it.isSelected =
                                            it.properties["orientation"] as Orientation == snapshotModel.orientation
                                    }
                                }
                            }
                            buttons.add(createOrientationButton(FontAwesome.Glyph.BARS, Orientation.VERTICAL))
                            buttons.add(createOrientationButton(FontAwesome.Glyph.BARS, Orientation.HORIZONTAL, true))
                        }
                    }
                }
            }
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            addClass(Styles.thinScrollPane)
            isFitToWidth = true
        }

        bottom = anchorpane {
            hbox {

                form {
                    fieldset {
                        field("Translations") {
                            slider(0, 5, 0) {
                                isSnapToTicks = true
                                isShowTickLabels = true
                                isShowTickMarks = true
                                blockIncrement = 1.0
                                majorTickUnit = 1.0
                                minorTickCount = 0
                                valueProperty().addListener { _, _, new -> value = new.toInt().toDouble() }
                                controller.numTranslationsProperty.bind(valueProperty())
                                setOnMouseReleased {
                                    runAsync {
                                        controller.loadTestVerses()
                                    } ui {
                                        verseGroupModel.item = VerseGroup(it)
                                    }
                                }
                            }
                        }
                        field("Verses") {
                            slider(0, 15, 0) {
                                isSnapToTicks = true
                                isShowTickLabels = true
                                isShowTickMarks = true
                                blockIncrement = 1.0
                                majorTickUnit = 5.0
                                minorTickCount = 4
                                prefWidth = 200.0
                                valueProperty().addListener { _, _, new -> value = new.toInt().toDouble() }
                                controller.numVersesProperty.bind(valueProperty())
                                setOnMouseReleased {
                                    runAsync {
                                        controller.loadTestVerses()
                                    } ui {
                                        verseGroupModel.item = VerseGroup(it)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            hbox {
                togglebutton("Test", selectFirst = false) {
                    selectedProperty().addListener { _, _, new ->
                        if (new) {
                            fire(OpenProjection(scope))
                        } else {
                            fire(CloseProjection(scope))
                        }
                    }
                    alignment = Pos.CENTER
                    enableWhen {
                        controller.numTranslationsProperty.ge(1)
                            .and(controller.numVersesProperty.ge(1))
                    }
                    paddingAll = 10
                }

                button(graphic = Styles.fontAwesome.create(FontAwesome.Glyph.SAVE)) {
                    action { saveSettings() }
                    paddingAll = 10
                }

                anchorpaneConstraints {
                    topAnchor = 5.0
                    rightAnchor = 5.0
                }
                spacing = 5.0
            }
        }
    }

    override fun onDock() {
        currentStage?.let { it.setOnCloseRequest {
                fontPicker.hide(Duration.millis(0.0))
            }
        }
        fire(InitTextAlignmentButton())
        fire(InitBoxLayoutButton())
    }

    override fun onUndock() {
        fire(LoadProjectionEditorSettings())
        if (fontPicker.isShowing)
            fontPicker.hide(Duration.millis(0.0))
    }

    private fun saveSettings() {
        projectionModel.commit()
        snapshotModel.commit()

        scope.savedProjectionModel.item = projectionModel.item
        scope.savedSnapshotModel.item = snapshotModel.item

        controller.savePreferencesToDB(snapshotModel.item)
    }

    private fun createAlignmentButton(glyph : FontAwesome.Glyph, textAlignment: TextAlignment)
        = ToggleButton().apply {
            graphic = Styles.fontAwesome.create(glyph)
            action { snapshotModel.textAlignment = textAlignment }
            properties["alignment"] = textAlignment
        }

    private fun createOrientationButton(glyph : FontAwesome.Glyph, orientation: Orientation, flipSide : Boolean = false)
        = ToggleButton().apply {
            graphic = Styles.fontAwesome.create(glyph).apply {
                if (flipSide) {
                    style { rotate = 90.deg }
                }
            }
            action { snapshotModel.orientation = orientation }
            properties["orientation"] = orientation
        }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)
