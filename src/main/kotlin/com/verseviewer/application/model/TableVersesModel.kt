package com.verseviewer.application.model

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue


class TableVersesModel() : ViewModel() {
    var cache = listOf<Verse>()
    val verses = FXCollections.observableArrayList<Verse>()
}

fun String.Companion.notAvailableValue() : String = "N.A"