package com.example.demo.model

import com.example.demo.model.datastructure.Range
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue
import java.io.Serializable

class Verse (id: Int = 0, translation: Translation = Translation(), book: String = "", chapter: Int = 0, verse: Range = Range(0..0), text: String = "") : Serializable  {
    val idProperty = SimpleIntegerProperty(this, "id", id)
    var id by idProperty

    val translationProperty = SimpleObjectProperty(this, "translation", translation)
    var translation by translationProperty

    val bookProperty = SimpleStringProperty(book)
    var book by bookProperty

    val chapterProperty = SimpleIntegerProperty(this, "chapter", chapter)
    var chapter by chapterProperty

    val verseProperty = SimpleObjectProperty<Range>(verse)
    var verse by verseProperty

    val textProperty = SimpleStringProperty(this, "text", text)
    var text: String by textProperty

    override fun toString(): String {
        return "${translation.abbreviation} $book ($chapter:$verse) $text"
    }
}

class VerseModel : ItemViewModel<Verse>() {
    val text = bind(Verse::text)
}