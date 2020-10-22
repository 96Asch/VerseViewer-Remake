package com.verseviewer.application.view.versebox

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.DBController
import com.verseviewer.application.controller.HelpPaneController
import com.verseviewer.application.model.Translation
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.FXCollections
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class HelpPane : View("Help", icon = Styles.fontAwesome.create(FontAwesome.Glyph.QUESTION_CIRCLE)) {

    private val controller : HelpPaneController by inject()
    private val widthProperty = SimpleDoubleProperty(0.0)

    override val root = drawer(multiselect = false) {
        val usage = item("Usage") {
            widthProperty.bind(widthProperty())
            scrollpane {
                form {
                    fieldset(controller.field1Text.first()) {
                        controller.field1Text.drop(1).forEach {
                            add(buildLabel(it))
                        }
                    }

                    fieldset(controller.field3Text.first()) {
                        controller.field2Text.drop(1).forEach {
                            add(buildLabel(it))
                        }
                    }

                    fieldset(controller.field3Text.first()) {
                        controller.field3Text.drop(1).forEach {
                            add(buildLabel(it))
                        }
                    }

                    fieldset(controller.field4Text.first()) {
                        controller.field4Text.drop(1).forEach {
                            add(buildLabel(it))
                        }
                    }
                }
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            }
        }

        val translations = item("Translations") {
            tableview(controller.translations) {
                readonlyColumn("Abbreviation", Translation::abbreviation)
                readonlyColumn("Name", Translation::name).enableTextWrap().remainingWidth()
                readonlyColumn("Lang", Translation::lang)
                columnResizePolicy = SmartResize.POLICY
            }
            expanded = true
        }

        usage.prefHeightProperty().bind(translations.heightProperty())
        items.addAll(usage, translations)
        useMaxSize = true
    }

    override fun onDock() {
        modalStage?.icons?.add(Image("icons/warning.png"))
    }

    private fun buildLabel(text : String) = label(text) {
        isWrapText = true
        prefWidthProperty().bind(widthProperty.minus(30))
    }
}
