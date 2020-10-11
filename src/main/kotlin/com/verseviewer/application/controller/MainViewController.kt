package com.verseviewer.application.controller

import com.verseviewer.application.model.User
import tornadofx.*

class MainViewController : Controller() {

    private val dbController : DBController by inject()

    fun loadPreference(user : User) = dbController.getUserPreference(user)

    fun loadUser() = dbController.getUsers().first()
}