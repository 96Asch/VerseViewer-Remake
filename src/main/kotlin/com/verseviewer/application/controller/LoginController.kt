package com.verseviewer.application.controller

import com.verseviewer.application.model.Preference
import com.verseviewer.application.model.User
import javafx.stage.FileChooser
import org.jetbrains.exposed.exceptions.ExposedSQLException
import tornadofx.Controller
import tornadofx.asObservable

class LoginController : Controller() {

    private val dbController : DBController by inject()

    val userList = mutableListOf<User>().asObservable()
    val dbExt = listOf(FileChooser.ExtensionFilter("SQLite Database", ".db")).toTypedArray()

    fun connectDatabase(path : String) {
        dbController.connectToDB(path)
    }

    fun validateDatabase() {

    }

    fun loadPreference(user : User): Preference {
        return Preference(dbController.getUserPreference(user))
    }

    fun getUsers() : List<User> {
        return dbController.getUsers()
    }
}