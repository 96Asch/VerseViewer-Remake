package com.verseviewer.application.model

import com.verseviewer.application.model.datastructure.VerseGroup
import javafx.beans.property.*
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue


class DisplayVersesModel : ItemViewModel<VerseGroup>() {
    var group = bind(VerseGroup::verses)
    var type = bind(VerseGroup::type)

    var sorted : List<List<Passage>> = listOf()

    init {
        itemProperty.addListener { _,_, new ->
            println("Item changed: $new")
            if (new != null)
                sorted = new.sortedByTranslation().values.toList()
        }
    }
}


