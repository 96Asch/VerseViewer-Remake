package com.verseviewer.application.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Orientation
import javafx.geometry.Rectangle2D
import javafx.scene.text.TextAlignment
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue

class ProjectionData() {
    val liveProperty = SimpleBooleanProperty(false)
    var isLive by liveProperty

    val widthProperty = SimpleDoubleProperty(0.0)
    var width by widthProperty

    val heightProperty = SimpleDoubleProperty(0.0)
    var height by heightProperty

    val screenBoundsProperty = SimpleObjectProperty<Rectangle2D>(Rectangle2D.EMPTY)
    var screenBounds by screenBoundsProperty


    override fun toString(): String {
        return "isLive: $isLive - width: $width - height - $height - screenBounds: $screenBounds"
    }
}

class ProjectionModel : ItemViewModel<ProjectionData>() {
    val liveProperty = bind(ProjectionData::liveProperty)
    val boxWidthProperty = bind(ProjectionData::widthProperty)
    val boxHeightProperty = bind(ProjectionData::heightProperty)
    val screenBoundsProperty = bind(ProjectionData::screenBoundsProperty)

    var isLive: Boolean by liveProperty
    var boxWidth: Number by boxWidthProperty
    var boxHeight: Number by boxHeightProperty
    var screenBounds: Rectangle2D by screenBoundsProperty
}

