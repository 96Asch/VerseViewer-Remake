package com.verseviewer.application.model

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

    val textMarginProperty = SimpleDoubleProperty(50.0)
    var textMargin by textMarginProperty

    val displayIndexProperty = SimpleIntegerProperty(0)
    var displayIndex by displayIndexProperty

    val liveProperty = SimpleBooleanProperty(false)
    var isLive by liveProperty



}


class ProjectionModel : ItemViewModel<ProjectionData>() {
    val displayIndex = bind(ProjectionData::displayIndex)
    val isLive = bind(ProjectionData::isLive)
    val font = bind(ProjectionData::fontProperty)
    val textMargin = bind(ProjectionData::textMargin)
}

