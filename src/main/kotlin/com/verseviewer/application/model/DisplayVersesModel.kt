package com.verseviewer.application.model

import com.verseviewer.application.model.datastructure.VerseGroup
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue


class DisplayVersesModel : ItemViewModel<VerseGroup>() {
    var group = bind(VerseGroup::versesProperty)
    var type = bind(VerseGroup::type)

    val header = group.stringBinding { passages ->
        val pass = passages?.first()
        "${pass?.translation?.abbreviation} - ${pass?.book} ${pass?.chapter}:${pass?.verse}"
    }

    val bodies = group.stringBinding { passages ->
        passages?.joinToString("\n") { it.text}
    }
}
