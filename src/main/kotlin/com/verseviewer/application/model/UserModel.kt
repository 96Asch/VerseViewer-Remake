package com.verseviewer.application.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.verseviewer.application.model.db.UserDAO
import com.verseviewer.application.model.db.Users
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
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

    constructor(user : UserDAO) : this(user.id.value, user.name) {
        val i = ByteArrayInputStream(user.layout.toByteArray(Charset.defaultCharset()))
        layout = i.toJSON().toModel()
    }

    fun layoutToString() = layout.toJSON().toString()

}

class NewUser() : User(-1, "New User")


class UserModel : ItemViewModel<User>() {
    val id = bind(User::idProperty)
    val name = bind(User::nameProperty)
    val layout = bind(User::layoutProperty)
}