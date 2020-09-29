package com.verseviewer.application.model

import com.verseviewer.application.model.datastructure.VerseGroup
import javafx.beans.property.*
import javafx.collections.FXCollections
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue


class DisplayVersesModel : ItemViewModel<VerseGroup>() {
    var group = bind(VerseGroup::verses)

    val sorted = SimpleListProperty<List<Passage>>(FXCollections.observableArrayList())

    init {
        itemProperty.addListener { _,_, new ->
            if (new != null) {
                sorted.setAll(new.sortedByTranslation().values)
            }
        }
    }
}


