package com.verseviewer.application.view.main

import com.verseviewer.application.model.User
import com.verseviewer.application.model.UserModel
import com.verseviewer.application.model.event.CreateNewUser
import com.verseviewer.application.model.event.LoginUser
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

class UserCellFragment : DataGridCellFragment<User>() {

    private val user = UserModel().bindTo(this)

    override val root = hbox {
        stackpane {
            circle(radius = 25.0) {
                fill = Color.BLUE
            }
            label(user.name.stringBinding {"${it?.first()}"})
        }
        label(user.name) {
            hboxConstraints {
                hGrow = Priority.ALWAYS
            }
        }
        onDoubleClick {
            user.item?.let{
                fire(LoginUser(user.item))
            }
        }
        paddingAll = 5.0
        spacing = 10.0
        alignment = Pos.CENTER_LEFT
    }
}