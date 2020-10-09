package com.verseviewer.application.view.editor

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.ProjectionEditorController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.FontModel
import com.verseviewer.application.model.ProjectionModel
import com.verseviewer.application.model.TextStyleModel
import com.verseviewer.application.model.datastructure.VerseGroup
import com.verseviewer.application.model.event.*
import com.verseviewer.application.model.scope.ProjectionEditorScope
import com.verseviewer.application.view.projection.Projection
import com.verseviewer.application.view.projection.ScalingPane
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ToggleButton
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import javafx.stage.Screen
import javafx.util.Duration
import org.controlsfx.control.PopOver
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.popover
import tornadofx.controlsfx.segmentedbutton
import tornadofx.controlsfx.showPopover


class ProjectionEditor : View() {

    override val scope = super.scope as ProjectionEditorScope

    private var projectionView : Fragment by singleAssign()

    private val controller : ProjectionEditorController by inject(FX.defaultScope)

    private val displayVersesModel : DisplayVersesModel by inject()
    private val fontModel : FontModel by inject()
    private val projectionModel : ProjectionModel by inject()
    private val textStyleModel : TextStyleModel by inject()

    private var fontPicker : PopOver by singleAssign()

    private val numTranslationsProperty = SimpleIntegerProperty(0)
    private val numVersesProperty = SimpleIntegerProperty(0)

    override val root = borderpane {
        fontModel.item = scope.savedFontModel.item
        projectionModel.item = scope.savedProjectionModel.item
        textStyleModel.item = scope.savedTextStyleModel.item

        projectionModel.screenBoundsProperty.value = Screen.getScreens().first().visualBounds

        projectionView = find<Projection>(mapOf("isCloseable" to false))

        center = ScalingPane(projectionView.root, projectionModel.screenBounds.width, projectionModel.screenBounds.height)

        val isSelectedProperty = SimpleBooleanProperty(false)
        
        right = scrollpane {

            form {
                fieldset("1. Secondary Screen") {
                    combobox(values = controller.screenList) {
                        selectionModel.selectedItemProperty().onChange {
                            if (it != null) {
                                projectionModel.displayIndex = it.index
                                projectionModel.screenBounds = it.screen.visualBounds
                            }
                        }
                        cellFormat {
                            text = "Display ${it.index} - [${it.screen.visualBounds.width}x${it.screen.visualBounds.height}]"
                        }
                        isSelectedProperty.bind(selectionModel.selectedItemProperty().isNotNull)
                    }
                }
                fieldset("2. Text") {
                    field("Alignment") {
                        segmentedbutton {
                            subscribe<InitTextAlignmentButton> {
                                buttons.filterIsInstance<ToggleButton>().forEach {
                                    if (it.properties.containsKey("alignment")) {
                                        it.isSelected = it.properties["alignment"] as TextAlignment == projectionModel.textAlignment
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

                        textProperty().bind(stringBinding(fontModel.familyProperty, fontModel.sizeProperty, fontModel.weightProperty, fontModel.postureProperty) {
                            "${fontModel.family}\n ${fontModel.size} [${fontModel.weight}-${fontModel.posture}]"
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
                        colorpicker { textStyleModel.fillProperty.bind(valueProperty()) }
                    }
                    field("Border Color") {
                        colorpicker { textStyleModel.strokeProperty.bind(valueProperty()) }
                    }
                    field("Border Width") {
                        slider(0,10,1) {
                            textStyleModel.strokeWidthProperty.bind(valueProperty())
                        }
                    }
                    field("Effect") {
                        segmentedbutton {}
                    }
                }
                fieldset("5. Multiple Translations") {
                    field("Orientation") {
                        segmentedbutton {
                            subscribe<InitBoxLayoutButton> {
                                buttons.filterIsInstance<ToggleButton>().forEach {
                                    if (it.properties.containsKey("orientation")) {
                                        it.isSelected = it.properties["orientation"] as Orientation == projectionModel.orientation
                                    }
                                }
                            }
                            buttons.add(createOrientationButton(FontAwesome.Glyph.BARS, Orientation.VERTICAL))
                            buttons.add(createOrientationButton(FontAwesome.Glyph.BARS, Orientation.HORIZONTAL, true))
                        }
                    }
                }
            }
        }

        bottom = anchorpane {

            hbox {
                anchorpaneConstraints {
                    hboxConstraints { marginRight = 10.0 }
                }
                label("Translations") {
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
                enableWhen { isSelectedProperty }
            }
        }
    }

    override fun onDock() {
        println("ondock PE")
        currentStage?.let { it.setOnCloseRequest {
                fontPicker.hide(Duration.millis(0.0))
            }
        }
        fire(InitTextAlignmentButton())
        fire(InitBoxLayoutButton())
    }

    override fun onUndock() {
        println("undock PE")
        println(scope.savedTextStyleModel.item.fillProperty.value)
        projectionModel.commit()
        fontModel.commit()
        textStyleModel.commit()

        scope.savedProjectionModel.item = projectionModel.item
        scope.savedFontModel.item = fontModel.item
        scope.savedTextStyleModel.item = textStyleModel.item
        println(scope.savedTextStyleModel.fill)

        fire(SaveProjectionEditorSettings())
    }

    private fun createAlignmentButton(glyph : FontAwesome.Glyph, textAlignment: TextAlignment) : ToggleButton {
        return ToggleButton().apply {
            graphic = Styles.fontAwesome.create(glyph)
            action { projectionModel.textAlignment = textAlignment }
            properties["alignment"] = textAlignment
        }
    }

    private fun createOrientationButton(glyph : FontAwesome.Glyph, orientation: Orientation, flipSide : Boolean = false) : ToggleButton{
        return ToggleButton().apply {
            graphic = Styles.fontAwesome.create(glyph).apply {
                if (flipSide) {
                    style { rotate = 90.deg }
                }
            }
            action { projectionModel.orientation = orientation }
            properties["orientation"] = orientation
        }
    }
}