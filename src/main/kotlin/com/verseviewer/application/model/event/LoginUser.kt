package com.verseviewer.application.model.event

import com.verseviewer.application.model.User
import tornadofx.FXEvent

class LoginUser(val user : User) : FXEvent()