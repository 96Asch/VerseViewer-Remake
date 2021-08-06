package com.verseviewer.application.model

import javafx.beans.property.*
import javafx.geometry.Orientation
import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

class Snapshot() {

    val idProperty = SimpleIntegerProperty(0)
    var id by idProperty

    val nameProperty = SimpleStringProperty("")
    val layoutProperty = SimpleObjectProperty(GridBuilder())

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

    constructor(id : Int,
                layout : String,
                displayIndex : Int,
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

        this.id = id

        val rawLayout = ByteArrayInputStream(layout.toByteArray(Charset.defaultCharset()))
        this.layoutProperty.value = rawLayout.toJSON().toModel()

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

    fun layoutToString() = layoutProperty.value.toJSON().toString()
}


class SnapshotModel : ItemViewModel<Snapshot>() {

    val idProperty = bind(Snapshot::idProperty)
    val nameProperty = bind(Snapshot::nameProperty)
    val layoutProperty = bind(Snapshot::layoutProperty)

    val displayIndexProperty = bind(Snapshot::displayIndexProperty)
    val orientationProperty = bind(Snapshot::orientationProperty)
    val textAlignmentProperty = bind(Snapshot::textAlignmentProperty)

    val sizeProperty = bind(Snapshot::fontSizeProperty, autocommit = true)
    val familyProperty = bind(Snapshot::fontFamilyProperty, autocommit = true)
    val weightProperty = bind(Snapshot::fontWeightProperty)
    val postureProperty = bind(Snapshot::fontPostureProperty)

    val fillProperty = bind(Snapshot::fillProperty)
    val strokeProperty = bind(Snapshot::strokeProperty)
    val strokeWidthProperty = bind(Snapshot::strokeWidthProperty)

    var id: Int by idProperty
    var name: String by nameProperty
    var layout: GridBuilder by layoutProperty

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
