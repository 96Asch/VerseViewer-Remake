package com.verseviewer.application.view.main

import com.verseviewer.application.controller.DBController
import javafx.geometry.Pos
import javafx.scene.Parent
import tornadofx.*

class NewUserDialog : View() {

    private val context = ValidationContext()
    private val dbController : DBController by inject()

    override val root = vbox {

        form {
            fieldset("Create new profile") {
                field("Profile name") {
                    textfield {
                        validator { if (it.isNullOrBlank()) error("Name cannot be empty") else null }
                    }
                }
            }
        }
        button("Create") {

        }
        alignment = Pos.CENTER_RIGHT
    }

}