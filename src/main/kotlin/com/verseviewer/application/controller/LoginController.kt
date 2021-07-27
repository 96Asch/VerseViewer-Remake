package com.verseviewer.application.controller

import com.verseviewer.application.model.Preference
import com.verseviewer.application.model.UiPreference
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
        val newUser = User(-1, name, jsonLayout.toPrettyString())
        dbController.addUser(newUser, Preference())
        dbController.addUiPreference(newUser, UiPreference(layout = jsonLayout.toPrettyString()))
    }

    fun loadPreference(user : User): Preference {
        return dbController.getPreference(user)
    }

    fun loadUiPreference(user: User): UiPreference {
        return dbController.getUiPreference(user)
    }

    fun getUsers() : List<User> {
        return dbController.getUsers()
    }
}