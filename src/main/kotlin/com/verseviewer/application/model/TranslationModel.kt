package com.verseviewer.application.model

import com.verseviewer.application.model.db.TranslationDAO
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class Translation(name: String = String.notAvailableValue(),
                  abbreviation: String = String.notAvailableValue(),
                  lang: String = String.notAvailableValue(),
                  isDeutercanonic: Boolean = false) {
    val nameProperty = SimpleStringProperty(name)
    var name by nameProperty

    val abbreviationProperty = SimpleStringProperty(abbreviation)
    var abbreviation by abbreviationProperty

    val langProperty = SimpleStringProperty(lang)
    var lang by langProperty

    val isDeutercanonicProperty = SimpleBooleanProperty(isDeutercanonic)
    var isDeutercanonic by isDeutercanonicProperty

    constructor(tl : TranslationDAO) : this(tl.id.value, tl.abbreviation, tl.lang, tl.isDeutercanonic)
}

class TranslationModel : ItemViewModel<Translation>() {
    val name = bind(Translation::nameProperty)
    val abbreviation = bind(Translation::abbreviationProperty)
    val lang = bind(Translation::langProperty)
    val isDeutercanonic = bind(Translation::isDeutercanonicProperty)
}




