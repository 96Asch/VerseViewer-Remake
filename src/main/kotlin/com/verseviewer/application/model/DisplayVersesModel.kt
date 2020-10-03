package com.verseviewer.application.model

import com.verseviewer.application.model.datastructure.VerseGroup
import javafx.beans.property.*
import javafx.collections.FXCollections
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue


class DisplayVersesModel : ItemViewModel<VerseGroup>() {
    var group = bind(VerseGroup::verses)
    var sorted = bind(VerseGroup::translationSorted)
}


