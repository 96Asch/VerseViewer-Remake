package com.verseviewer.application.model

import com.verseviewer.application.model.datastructure.VerseGroup
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue


class DisplayVersesModel : ItemViewModel<VerseGroup>() {
    val groupProperty = SimpleObjectProperty<VerseGroup>()
    var group by groupProperty

    val isLiveProperty = SimpleBooleanProperty(false)
    var isLive by isLiveProperty

}
