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
    val displayIndexProperty = SimpleIntegerProperty(0)
    var displayIndex by displayIndexProperty

    val liveProperty = SimpleBooleanProperty(false)
    var isLive by liveProperty

    val orientationProperty = SimpleObjectProperty<Orientation>(Orientation.HORIZONTAL)
    var orientation: Orientation by orientationProperty

    val widthProperty = SimpleDoubleProperty(0.0)
    var width by widthProperty

    val heightProperty = SimpleDoubleProperty(0.0)
    var height by heightProperty

    val screenBoundsProperty = SimpleObjectProperty<Rectangle2D>(Rectangle2D(0.0,0.0,0.0,0.0))
    var screenBounds by screenBoundsProperty

    val textAlignmentProperty = SimpleObjectProperty<TextAlignment>(TextAlignment.LEFT)
    var textAlignment by textAlignmentProperty

}


class ProjectionModel : ItemViewModel<ProjectionData>() {
    val displayIndexProperty = bind(ProjectionData::displayIndexProperty)
    val liveProperty = bind(ProjectionData::liveProperty)
    val orientationProperty = bind(ProjectionData::orientationProperty)
    val boxWidthProperty = bind(ProjectionData::widthProperty)
    val boxHeightProperty = bind(ProjectionData::heightProperty)
    val screenBoundsProperty = bind(ProjectionData::screenBoundsProperty)
    val textAlignmentProperty = bind(ProjectionData::textAlignmentProperty)

    var displayIndex: Number by displayIndexProperty
    var isLive: Boolean by liveProperty
    var orientation: Orientation by orientationProperty
    var boxWidth: Number by boxWidthProperty
    var boxHeight: Number by boxHeightProperty
    var screenBounds: Rectangle2D by screenBoundsProperty
    var textAlignment: TextAlignment by textAlignmentProperty
}

