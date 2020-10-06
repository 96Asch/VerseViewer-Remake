package com.verseviewer.application.model

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.*

class FontData(size : Number = 20.0, family : String = "Arial", weight : FontWeight = FontWeight.NORMAL, posture: FontPosture = FontPosture.REGULAR) {
    val fontSizeProperty = SimpleObjectProperty<Number>(size)
    var fontSize by fontSizeProperty

    val fontFamilyProperty = SimpleStringProperty(family)
    var fontFamily by fontFamilyProperty

    val fontWeightProperty = SimpleObjectProperty<FontWeight>(weight)
    var fontWeight by fontWeightProperty

    val fontPostureProperty = SimpleObjectProperty<FontPosture>(posture)
    var fontPosture by fontPostureProperty
}

class FontModel : ItemViewModel<FontData>() {
    val sizeProperty = bind(FontData::fontSize)
    val familyProperty = bind(FontData::fontFamily)
    val weightProperty = bind(FontData::fontWeight)
    val postureProperty = bind(FontData::fontPosture)

    var size by sizeProperty
    var family by familyProperty
    var weight by weightProperty
    var posture by postureProperty

}