package com.verseviewer.application.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

class User() {
    val idProperty = SimpleIntegerProperty(-1)
    var id by idProperty

    val nameProperty = SimpleStringProperty("New User")
    var name: String by nameProperty

    val layoutProperty = SimpleObjectProperty(GridBuilder())
    var layout: GridBuilder by layoutProperty

    constructor(id : Int = -1, name : String, layout : String) : this() {
        this.id = id
        this.name = name
        val rawLayout = ByteArrayInputStream(layout.toByteArray(Charset.defaultCharset()))
        this.layout = rawLayout.toJSON().toModel()
    }

    fun layoutToString() = layout.toJSON().toString()

}

class UserModel : ItemViewModel<User>() {
    val id = bind(User::idProperty)
    val name = bind(User::nameProperty)
    val layout = bind(User::layoutProperty)
}