package com.example.demo.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
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
}

class TranslationModel : ItemViewModel<Translation>() {
    val name = bind(Translation::nameProperty)
    val abbreviation = bind(Translation::abbreviationProperty)
    val lang = bind(Translation::langProperty)
    val isDeutercanonic = bind(Translation::isDeutercanonicProperty)
}




