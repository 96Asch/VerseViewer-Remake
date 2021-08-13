package com.verseviewer.application.view.editor

import com.verseviewer.application.model.SnapshotModel
import com.verseviewer.application.controller.FontPickerController
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.Pos
import tornadofx.*

class FontPicker : Fragment() {

    private val controller : FontPickerController by inject()
    private val preferenceModel : SnapshotModel by inject()

    override val root = gridpane {

        paddingAll = 10.0
        val valueProperty = SimpleDoubleProperty()

        label("Family") {
            alignment = Pos.CENTER
            useMaxWidth = true
            gridpaneConstraints {
                columnRowIndex(0,0)
            }
        }

        listview(controller.fontfamilyList) {
            maxWidth = 175.0
            maxHeight = 200.0
            selectionModel.select(preferenceModel.family)
            bindSelected(preferenceModel.familyProperty)
            gridpaneConstraints {
                marginTop = 10.0
                columnRowIndex(0,1)
                rowSpan = 4
            }
        }

        label("Maximum Size") {
            alignment = Pos.CENTER
            useMaxWidth = true
            gridpaneConstraints {
                columnRowIndex(1,0)
            }
        }

        slider(10, 80, preferenceModel.size) {
            isSnapToTicks = true
            isShowTickLabels = true
            isShowTickMarks = true
            blockIncrement = 1.0
            majorTickUnit = 10.0
            valueProperty.bind(valueProperty())
            onMouseReleased = EventHandler {
                preferenceModel.size = this.value
            }
            gridpaneConstraints {
                marginTop = 10.0
                columnRowIndex(1,1)
            }
        }
        label(valueProperty.stringBinding {it?.toInt().toString()}) {
            useMaxWidth = true
            alignment = Pos.CENTER
            gridpaneConstraints {
                marginTop = 10.0
                columnRowIndex(1,2)
            }
        }
        label("Styling") {
            alignment = Pos.CENTER
            useMaxWidth = true
            gridpaneConstraints {
                marginTop = 20.0
                columnRowIndex(1,3)
            }
        }
        form {
            fieldset {
                field("Weight") { choicebox(preferenceModel.weightProperty) { items = controller.fontWeightList  } }
                field("Posture") { choicebox(preferenceModel.postureProperty) { items = controller.fontPostureList  }  }
            }
            gridpaneConstraints {
                alignment = Pos.CENTER
                hAlignment = HPos.CENTER
                columnRowIndex(1,4)
            }
        }

    }
}