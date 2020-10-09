package com.verseviewer.application.model

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.effect.Effect
import javafx.scene.paint.Color
import tornadofx.*

class TextStyle {

    val fillProperty = SimpleObjectProperty(Color.valueOf("black"))
    val strokeProperty = SimpleObjectProperty(Color.valueOf("black"))
    val strokeWidthProperty = SimpleDoubleProperty(0.0)
    val effectProperty = SimpleObjectProperty<Effect>()

}


class TextStyleModel() : ItemViewModel<TextStyle>() {
    val fillProperty = bind(TextStyle::fillProperty)
    val strokeProperty = bind(TextStyle::strokeProperty)
    val strokeWidthProperty = bind(TextStyle::strokeWidthProperty)
    val effectProperty = bind(TextStyle::effectProperty)

    var fill by fillProperty
    var stroke by strokeProperty
    var strokeWidth by strokeWidthProperty
    var effect by effectProperty
}