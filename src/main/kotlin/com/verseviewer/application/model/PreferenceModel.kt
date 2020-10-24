package com.verseviewer.application.model

import javafx.beans.property.*
import javafx.geometry.Orientation
import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue

class Preference() {

    val displayIndexProperty = SimpleIntegerProperty(0)
    val orientationProperty = SimpleObjectProperty<Orientation>(Orientation.HORIZONTAL)
    val textAlignmentProperty = SimpleObjectProperty<TextAlignment>(TextAlignment.LEFT)

    val fontSizeProperty = SimpleObjectProperty<Number>(50.0)
    val fontFamilyProperty = SimpleStringProperty("Arial")
    val fontWeightProperty = SimpleObjectProperty<FontWeight>(FontWeight.NORMAL)
    val fontPostureProperty = SimpleObjectProperty<FontPosture>(FontPosture.REGULAR)

    val fillProperty = SimpleObjectProperty(Color.valueOf("black"))
    val strokeProperty = SimpleObjectProperty(Color.valueOf("black"))
    val strokeWidthProperty = SimpleDoubleProperty(0.0)

    constructor(displayIndex : Int,
                orientationStr : String,
                textAlignmentStr : String,
                fontSize : Double,
                fontFamily : String,
                fontPostureStr : String,
                fontWeightStr : String,
                fillStr : String,
                strokeStr : String,
                strokeWidth : Double
    ) : this() {

        displayIndexProperty.value = displayIndex
        orientationProperty.value = Orientation.valueOf(orientationStr)
        textAlignmentProperty.value = TextAlignment.valueOf(textAlignmentStr)

        fontSizeProperty.value = fontSize
        fontFamilyProperty.value = fontFamily
        fontPostureProperty.value = FontPosture.valueOf(fontPostureStr)
        fontWeightProperty.value = FontWeight.valueOf(fontWeightStr)

        fillProperty.value = Color.valueOf(fillStr)
        strokeProperty.value = Color.valueOf(strokeStr)
        strokeWidthProperty.value = strokeWidth
    }
}


class PreferenceModel : ItemViewModel<Preference>() {

    val displayIndexProperty = bind(Preference::displayIndexProperty)
    val orientationProperty = bind(Preference::orientationProperty)
    val textAlignmentProperty = bind(Preference::textAlignmentProperty)

    val sizeProperty = bind(Preference::fontSizeProperty, autocommit = true)
    val familyProperty = bind(Preference::fontFamilyProperty, autocommit = true)
    val weightProperty = bind(Preference::fontWeightProperty)
    val postureProperty = bind(Preference::fontPostureProperty)

    val fillProperty = bind(Preference::fillProperty)
    val strokeProperty = bind(Preference::strokeProperty)
    val strokeWidthProperty = bind(Preference::strokeWidthProperty)

    var displayIndex: Number by displayIndexProperty
    var orientation: Orientation by orientationProperty
    var textAlignment: TextAlignment by textAlignmentProperty

    var size: Number by sizeProperty
    var family: String by familyProperty
    var weight: FontWeight by weightProperty
    var posture: FontPosture by postureProperty

    var fill by fillProperty
    var stroke by strokeProperty
    var strokeWidth by strokeWidthProperty
}
