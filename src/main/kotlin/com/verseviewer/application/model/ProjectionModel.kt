package com.verseviewer.application.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue

class ProjectionData(displayIndex : Int = 0, isVisible : Boolean = false) {
    val displayIndexProperty = SimpleIntegerProperty(displayIndex)
    var displayIndex by displayIndexProperty

    val isVisibleProperty = SimpleBooleanProperty(isVisible)
    var isVisible by isVisibleProperty
}


class ProjectionModel : ItemViewModel<ProjectionData>() {
    val displayIndex = bind(ProjectionData::displayIndex)
    val isVisible = bind(ProjectionData::isVisible)
}

