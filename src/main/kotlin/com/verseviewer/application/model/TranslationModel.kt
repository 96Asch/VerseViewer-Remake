package com.verseviewer.application.model

import com.verseviewer.application.model.db.TranslationDAO
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject

class Translation() : JsonModel {
    val nameProperty = SimpleStringProperty("")
    var name by nameProperty

    val abbreviationProperty = SimpleStringProperty("")
    var abbreviation by abbreviationProperty

    val langProperty = SimpleStringProperty("")
    var lang by langProperty

    val isDeutercanonicProperty = SimpleBooleanProperty(false)
    var isDeutercanonic by isDeutercanonicProperty

    constructor(name : String,
                abbreviation : String,
                lang : String,
                isDeutercanonic : Boolean) : this()
    {
        this.name = name
        this.abbreviation = abbreviation
        this.lang = lang
        this.isDeutercanonic = isDeutercanonic
    }

    constructor(dao : TranslationDAO) : this() {
        name = dao.id.value
        abbreviation = dao.abbreviation
        lang = dao.lang
        isDeutercanonic = dao.isDeutercanonic
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            name = string("name") ?: ""
            abbreviation = string("abbreviation") ?: ""
            lang = string("language") ?: ""
            isDeutercanonic = bool("isDeutercanonic") ?: false
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("name", name)
            add("abbreviation", abbreviation)
            add("language", lang)
            add("isDeutercanonic", isDeutercanonic)
        }
    }

    override fun toString(): String {
        return "$abbreviation - $name ($lang)"
    }
}