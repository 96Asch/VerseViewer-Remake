package com.example.demo.model

import com.example.demo.model.datastructure.VerseGroup
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue


class DisplayVersesModel : ItemViewModel<VerseGroup>() {
    val groupProperty = SimpleObjectProperty<VerseGroup>()
    var group by groupProperty

    val isLiveProperty = SimpleBooleanProperty(false)
    var isLive by isLiveProperty

}
