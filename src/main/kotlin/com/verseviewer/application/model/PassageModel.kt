package com.verseviewer.application.model

import com.verseviewer.application.model.datastructure.Range
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject


class Passage() : JsonModel   {

    val idProperty = SimpleIntegerProperty(0)
    var id by idProperty

    val translationProperty = SimpleObjectProperty(Translation())
    var translation by translationProperty

    val bookProperty = SimpleStringProperty("")
    var book by bookProperty

    val chapterProperty = SimpleIntegerProperty(0)
    var chapter by chapterProperty

    val verseProperty = SimpleObjectProperty<Range>(Range(0,0))
    var verse by verseProperty

    val textProperty = SimpleStringProperty("")
    var text: String by textProperty

    constructor(id : Int,
                translation : Translation,
                book : String,
                chapter : Int,
                verse : Range,
                text : String) : this()
    {
        this.id = id
        this.translation = translation
        this.book = book
        this.chapter = chapter
        this.verse = verse
        this.text = text
    }

    fun isAdjacentTo(other : Passage) = (chapter == other.chapter && verse.last+1 == other.verse.first)

    override fun updateModel(json: JsonObject) {
        with(json) {
            id = int("id") ?: 0
            translation = getJsonObject("translation").toModel()
            book = string("book") ?: ""
            chapter = int("chapter") ?: 0
            verse = getJsonObject("verse").toModel()
            text = string("text") ?: ""
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("translation", translation.toJSON())
            add("book", book)
            add("chapter", chapter)
            add("verse", verse.toJSON())
            add("text", text)
        }
    }

    override fun toString(): String {
        return "${translation.abbreviation} $book ($chapter:$verse) $text"
    }
}

class PassageModel : ItemViewModel<Passage>()  {
    val text = bind(Passage::text)
}