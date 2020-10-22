package com.verseviewer.application.view.main

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.LoginController
import com.verseviewer.application.model.NewUser
import com.verseviewer.application.model.PreferenceModel
import com.verseviewer.application.model.UserModel
import com.verseviewer.application.model.event.*
import com.verseviewer.application.util.showForSeconds
import javafx.beans.property.SimpleStringProperty
import javafx.stage.Modality
import javafx.stage.StageStyle
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.notificationPane

class Login : View() {

    private val controller : LoginController by inject()
    private val databasePathProperty = SimpleStringProperty()
    private val newUser = NewUser()
    private val userModel : UserModel by inject()
    private val preferenceModel : PreferenceModel by inject()

    override val root = notificationPane {
        content = borderpane {

            center = vbox {
                datagrid(controller.userList) {
                    maxCellsInRow = 1
                    cellWidth = 300.0
                    cellHeight = 75.0
                    prefWidth = 325.0
                    cellFragment<UserCellFragment>()

                    subscribe<RefreshUsers> {
                        runAsyncWithOverlay {
                            controller.getUsers()
                        } ui {
                            controller.userList.setAll(it)
                            controller.userList.add(newUser)
                        }
                    }
                }

                button(graphic = Styles.fontAwesome.create(FontAwesome.Glyph.PLUS_CIRCLE)) {
                    action {

                    }


                }

                hbox {
                    label {
                        textProperty().bind(databasePathProperty)
                    }
                    button(graphic = Styles.fontAwesome.create(FontAwesome.Glyph.EXCHANGE)) {
                        action {
                            val locations = chooseFile("Choose database", controller.dbExt)
                            if (locations.isNotEmpty()) {
                                println("ooo")
                                controller.connectDatabase(locations.first().absolutePath)
                                preferences("VerseViewer") {
                                    put("db_location", locations.first().absolutePath)
                                    databasePathProperty.value = locations.first().absolutePath
                                }
                                fire(RefreshUsers())
                            }
                        }
                    }
                }
                spacing = 10.0
            }
        }
        subscribe<SendDBNotification> {
            showForSeconds(NotificationType.ERROR, it.message, 3)
        }

        subscribe<LoginUser> {
            runAsync {
                userModel.item = it.user
                preferenceModel.item = controller.loadPreference(it.user)
            } ui {
                replaceWith<MainView>()
            }
        }

        subscribe<CreateNewUser> {
            dialog("Create new profile") {
                val model = ViewModel()
                val note = model.bind { SimpleStringProperty() }

                field("Name") {
                    textfield(note) {
                        required()
                        validator { if (it in controller.userList.map { user -> user.name }) error("$it is already used") else null }
                        whenDocked { requestFocus() }
                    }
                }
                buttonbar {
                    button("Create").action {
                        model.commit { println("Commit") }
                    }
                }
            }
        }
    }

    override fun onDock() {
        preferences("VerseViewer") {
            databasePathProperty.value = get("db_location", "NO_PATH")
        }
        controller.connectDatabase(databasePathProperty.value)
        fire(RefreshUsers())
    }
}