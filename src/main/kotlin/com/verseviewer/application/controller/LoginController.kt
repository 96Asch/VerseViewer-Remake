package com.verseviewer.application.controller

import com.verseviewer.application.model.Preference
import com.verseviewer.application.model.User
import javafx.stage.FileChooser
import tornadofx.*

class LoginController : Controller() {

    private val dbController : DBController by inject()

    val userList = mutableListOf<User>().asObservable()
    val dbExt = listOf(FileChooser.ExtensionFilter("SQLite Database", ".db")).toTypedArray()
    private val defaultJson = "/layout/default.json"
    val jsonLayout = resources.json(defaultJson)

    fun connectDatabase(path : String) {
        dbController.connectToDB(path)
    }

    fun createNewUser(name : String) {
        dbController.addUser(User(-1, name, jsonLayout.toPrettyString()), Preference())
    }

    fun loadPreference(user : User): Preference {
        return dbController.getPreference(user)
    }

    fun getUsers() : List<User> {
        return dbController.getUsers()
    }
}