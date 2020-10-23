package com.verseviewer.application.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

open class User(id : Int = 0, name : String = "", layout : GridBuilder = GridBuilder()) {
    val idProperty = SimpleIntegerProperty(id)
    var id by idProperty

    val nameProperty = SimpleStringProperty(name)
    var name: String by nameProperty

    val layoutProperty = SimpleObjectProperty(layout)
    var layout: GridBuilder by layoutProperty

    constructor(id : Int, name : String, layout : String) : this(id, name) {
        val i = ByteArrayInputStream(layout.toByteArray(Charset.defaultCharset()))
        this.layout = i.toJSON().toModel()
    }

    fun layoutToString() = layout.toJSON().toString()

}

class UserModel : ItemViewModel<User>() {
    val id = bind(User::idProperty)
    val name = bind(User::nameProperty)
    val layout = bind(User::layoutProperty)
}