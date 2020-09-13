package com.verseviewer.application.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class Book(book_id : Int = 0, name: String = "", type : String = "") {
    val book_idProperty = SimpleIntegerProperty(book_id)
    var book_id by book_idProperty

    val nameProperty = SimpleStringProperty(name)
    var name by nameProperty

    val typeProperty = SimpleStringProperty(type)
    var type by typeProperty
}

class BookModel : ItemViewModel<Book>() {
    val book_id = bind(Book::book_id)
    val name = bind(Book::name)
    val type = bind(Book::type)
}

