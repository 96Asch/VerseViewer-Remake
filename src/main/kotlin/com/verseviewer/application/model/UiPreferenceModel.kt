package com.verseviewer.application.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.*
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

class UiPreference() {
    val layoutProperty = SimpleObjectProperty(GridBuilder())
    var layout: GridBuilder by layoutProperty

    val tileColorProperty = SimpleObjectProperty<Color>(Color.WHITESMOKE)
    val roundedCornerProperty = SimpleBooleanProperty(true)

    val fontSizeProperty = SimpleObjectProperty<Number>(12.0)
    val fontFamilyProperty = SimpleStringProperty("Tahoma")
    val fontWeightProperty = SimpleObjectProperty<FontWeight>(FontWeight.NORMAL)
    val fontPostureProperty = SimpleObjectProperty<FontPosture>(FontPosture.REGULAR)

    constructor(layout : String,
                tileColor : String,
                roundedCorner : Boolean,
                fontSize : Double,
                fontFamily : String,
                fontWeight : String,
                fontPosture: String) : this() {
        val rawLayout = ByteArrayInputStream(layout.toByteArray(Charset.defaultCharset()))
        this.layout = rawLayout.toJSON().toModel()
        tileColorProperty.value = Color.valueOf(tileColor)
        roundedCornerProperty.value = roundedCorner
        fontSizeProperty.value = fontSize
        fontFamilyProperty.value = fontFamily
        fontWeightProperty.value = FontWeight.valueOf(fontWeight)
        fontPostureProperty.value = FontPosture.valueOf(fontPosture)
    }

    constructor(layout : String) : this() {
        val rawLayout = ByteArrayInputStream(layout.toByteArray(Charset.defaultCharset()))
        this.layout = rawLayout.toJSON().toModel()
    }



    fun layoutToString() = layoutProperty.value.toJSON().toString()
}

class UiPreferenceModel : ItemViewModel<UiPreference>() {
    val layout = bind(UiPreference::layoutProperty)
    val tileColor = bind(UiPreference::tileColorProperty)
    val roundedCorner = bind(UiPreference::roundedCornerProperty)
    val fontSize = bind(UiPreference::fontSizeProperty)
    val fontFamily = bind(UiPreference::fontFamilyProperty)
    val fontWeight = bind(UiPreference::fontWeightProperty)
    val fontPosture = bind(UiPreference::fontPostureProperty)
}