package com.verseviewer.application.model

import com.verseviewer.application.model.db.PreferenceDAO
import javafx.beans.binding.NumberExpression
import javafx.beans.property.*
import javafx.geometry.Orientation
import javafx.scene.effect.Effect
import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue

class Preference(id : Int = 0) {
    private val internalId = ReadOnlyIntegerWrapper(id)
    val idProperty : ReadOnlyIntegerProperty = internalId.readOnlyProperty

    val nameProperty = SimpleStringProperty("New Preset")

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

    constructor(dao: PreferenceDAO) : this(dao.id.value) {

        nameProperty.value = dao.name

        displayIndexProperty.value = dao.display
        orientationProperty.value = Orientation.valueOf(dao.orientation)
        textAlignmentProperty.value = TextAlignment.valueOf(dao.textAlignment)

        fontSizeProperty.value = dao.fontSize
        fontFamilyProperty.value = dao.fontFamily
        fontPostureProperty.value = FontPosture.valueOf(dao.fontPosture)
        fontWeightProperty.value = FontWeight.valueOf(dao.fontWeight)

        fillProperty.value = Color.valueOf(dao.textFill)
        strokeProperty.value = Color.valueOf(dao.textStroke)
        strokeWidthProperty.value = dao.textStrokeWidth
    }
}


class PreferenceModel : ItemViewModel<Preference>() {

    val nameProperty = bind(Preference::nameProperty)

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

    var name: String by nameProperty

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
