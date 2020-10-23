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
    val json = resources.json(defaultJson)

    fun connectDatabase(path : String) {
        dbController.connectToDB(path)
    }

    fun validateDatabase() {

    }

    fun createNewUser(name : String) {
        val newUser = User(0,name, json.toString())
        dbController.addUser(newUser)
        dbController.addPreference(newUser, Preference())
    }

    fun loadPreference(user : User): Preference {
        return Preference(dbController.getUserPreference(user))
    }

    fun getUsers() : List<User> {
        return dbController.getUsers()
    }
}