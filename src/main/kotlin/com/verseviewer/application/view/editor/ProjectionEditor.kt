package com.verseviewer.application.view.editor

import com.verseviewer.application.controller.ProjectionEditorController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.FontData
import com.verseviewer.application.model.FontModel
import com.verseviewer.application.model.ProjectionModel
import com.verseviewer.application.model.datastructure.VerseGroup
import com.verseviewer.application.model.event.CloseProjection
import com.verseviewer.application.model.event.OpenProjection
import com.verseviewer.application.view.projection.BoxLayout
import com.verseviewer.application.view.projection.Projection
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import javafx.stage.Screen
import javafx.util.Duration
import org.controlsfx.control.PopOver
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import tornadofx.*
import tornadofx.controlsfx.popover
import tornadofx.controlsfx.segmentedbutton
import tornadofx.controlsfx.showPopover


class ProjectionEditor : Fragment() {

    private val displayVersesModel : DisplayVersesModel by inject()
    private val projectionModel : ProjectionModel by inject()
    private var projectionView : Fragment by singleAssign()
    private val controller : ProjectionEditorController by inject()
    private val fontModel : FontModel by inject()

    private var popOver : PopOver by singleAssign()

    private val numTranslationsProperty = SimpleIntegerProperty(0)
    private val numVersesProperty = SimpleIntegerProperty(0)

    override val root = hbox {

        projectionModel.screenBoundsProperty.value = Screen.getScreens()[1].visualBounds

        val scaler = 0.6
        val glyph = GlyphFontRegistry.font("FontAwesome")
        projectionView = find<Projection>(mapOf("isCloseable" to false))

        projectionView.root.apply {
            scaleX = scaler
            scaleY = scaler
            translateY = (-1 * projectionModel.screenBoundsProperty.value.height / 5)
            translateX = (-1 * projectionModel.screenBoundsProperty.value.width / 5)
        }

        vbox {
            pane {
                vboxConstraints { vGrow = Priority.ALWAYS }
                hboxConstraints { hGrow = Priority.ALWAYS }
                style {
                    borderColor += box(
                            top = Color.RED,
                            right = Color.DARKGREEN,
                            left = Color.ORANGE,
                            bottom = Color.PURPLE
                    )
                }
                maxHeight = projectionModel.screenBoundsProperty.value.height * scaler
                maxWidth = projectionModel.screenBoundsProperty.value.width * scaler
                minHeight = projectionModel.screenBoundsProperty.value.height * scaler
                minWidth = projectionModel.screenBoundsProperty.value.width * scaler
                add(projectionView.root)
            }
            anchorpane {
                hbox {
                    anchorpaneConstraints {
                        topAnchor = 5.0
                        leftAnchor = 5.0
                    }
                    paddingAll = 10.0
                    label("Num Translations") {
                        hboxConstraints { marginRight = 10.0 }
                    }

                    slider(0, 5, 0) {
                        numTranslationsProperty.bind(valueProperty())
                        isSnapToTicks = true
                        isShowTickLabels = true
                        isShowTickMarks = true
                        blockIncrement = 1.0
                        majorTickUnit = 1.0
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

                    label("Num Verses") {
                        hboxConstraints { marginRight = 10.0 }
                    }

                    slider(0, 15, 0) {
                        numVersesProperty.bind(valueProperty())
                        isSnapToTicks = true
                        isShowTickLabels = true
                        isShowTickMarks = true
                        blockIncrement = 1.0
                        majorTickUnit = 5.0
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

                togglebutton("Live", selectFirst = false) {
                    anchorpaneConstraints {
                        topAnchor = 5.0
                        rightAnchor = 5.0
                    }
                    selectedProperty().addListener { _, _, new ->
                        if (new) {
                            fire(OpenProjection())
                        } else {
                            fire(CloseProjection())
                        }
                    }
                }
            }
        }

        fontModel.item = FontData(50.0, "Tahoma", FontWeight.BOLD, FontPosture.ITALIC)
        projectionModel.boxLayout = BoxLayout.VERTICAL

        println(fontModel.itemProperty.value.fontFamily)
        scrollpane {
//            hboxConstraints { hGrow = Priority.ALWAYS }
            form {
                fieldset("1. Text") {
                    field("Alignment") {
                        segmentedbutton {

                            buttons.add(togglebutton {
                                graphic = glyph.create(FontAwesome.Glyph.ALIGN_LEFT)
                                action { projectionModel.textAlignment = TextAlignment.LEFT }
                            })
                            buttons.add(togglebutton {
                                graphic = glyph.create(FontAwesome.Glyph.ALIGN_CENTER)
                                action { projectionModel.textAlignment = TextAlignment.CENTER }
                            })
                            buttons.add(togglebutton {
                                graphic = glyph.create(FontAwesome.Glyph.ALIGN_RIGHT)
                                action { projectionModel.textAlignment = TextAlignment.RIGHT }
                            })
                            buttons.add(togglebutton {
                                graphic = glyph.create(FontAwesome.Glyph.ALIGN_JUSTIFY)
                                action { projectionModel.textAlignment = TextAlignment.JUSTIFY }
                            })
                        }
                    }
                }
                fieldset("2. Font") {
                    field("Family") {
                        button {
                            textProperty().bind(fontModel.stringProperty)
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
                            buttons.add(togglebutton { graphic = glyph.create(FontAwesome.Glyph.BARS)
                                action {
                                    projectionModel.boxLayout = BoxLayout.HORIZONTAL
                                }
                            })
                            buttons.add(togglebutton { graphic = glyph.create(FontAwesome.Glyph.BARS).apply {
                                style {
                                    rotate = 90.deg
                                }
                                action {
                                    projectionModel.boxLayout = BoxLayout.VERTICAL
                                }
                            }})
                        }
                    }
                }
            }
        }
    }

    override fun onDock() {

        currentStage?.let { it.setOnCloseRequest {
                popOver.hide(Duration.millis(0.0));
            }
        }

    }
}