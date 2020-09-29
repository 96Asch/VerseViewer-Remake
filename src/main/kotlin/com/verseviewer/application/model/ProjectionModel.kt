package com.verseviewer.application.model

import com.verseviewer.application.view.projection.BoxLayout
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.text.Font
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue

class ProjectionData() {
    val fontProperty = SimpleObjectProperty<Font>(Font.font(50.0))
    var font by fontProperty

    val displayIndexProperty = SimpleIntegerProperty(0)
    var displayIndex by displayIndexProperty

    val liveProperty = SimpleBooleanProperty(false)
    var isLive by liveProperty

    val boxLayoutProperty = SimpleObjectProperty<BoxLayout>(BoxLayout.HORIZONTAL)
    var boxLayout by boxLayoutProperty

    val widthProperty = SimpleDoubleProperty(0.0)
    var width by widthProperty

    val heightProperty = SimpleDoubleProperty(0.0)
    var height by heightProperty

}


class ProjectionModel : ItemViewModel<ProjectionData>() {
    val displayIndex = bind(ProjectionData::displayIndex)
    val isLive = bind(ProjectionData::isLive)
    val font = bind(ProjectionData::fontProperty)
    val boxLayout = bind(ProjectionData::boxLayoutProperty)
    val width = bind(ProjectionData::width)
    val height = bind(ProjectionData::height)
}

