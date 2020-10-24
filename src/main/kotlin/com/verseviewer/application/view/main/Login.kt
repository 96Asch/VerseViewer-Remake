package com.verseviewer.application.view.main

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.LoginController
import com.verseviewer.application.model.PreferenceModel
import com.verseviewer.application.model.UserModel
import com.verseviewer.application.model.event.*
import com.verseviewer.application.util.showForSeconds
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.notificationPane

class Login : View() {

    private val controller : LoginController by inject()
    private val databasePathProperty = SimpleStringProperty()
    private val userModel : UserModel by inject()
    private val preferenceModel : PreferenceModel by inject()

    override val root = notificationPane {
        setPrefSize(800.0, 500.0)
        content = borderpane {

            center = label("Verse Viewer")

            right = vbox {
                button {
                    graphic = hbox {
                        stackpane {
                            rectangle(0,0, 40, 40)
                            label(graphic = Styles.fontAwesome.create(FontAwesome.Glyph.PLUS_SQUARE))
                        }
                        label("Add new profile") {
                            hboxConstraints {
                                hGrow = Priority.ALWAYS
                            }
                        }
                        spacing = 10.0
                        alignment = Pos.CENTER_LEFT
                    }
                    action {
                        fire(CreateNewUser())
                    }
                    addClass(Styles.transparentButton)
                }

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
                        }
                    }
                }

                button {
                    addClass(Styles.transparentButton)
                    graphic = hbox {
                        stackpane {
                            rectangle(0,0, 40, 40)
                            label(graphic = Styles.fontAwesome.create(FontAwesome.Glyph.RECYCLE))
                        }
                        label {
                            textProperty().bind(databasePathProperty)
                            hboxConstraints {
                                hGrow = Priority.ALWAYS
                            }
                        }
                        spacing = 10.0
                        alignment = Pos.CENTER_LEFT
                    }
                    action {
                        val locations = chooseFile("Choose database", controller.dbExt)
                        if (locations.isNotEmpty()) {
                            controller.connectDatabase(locations.first().absolutePath)
                            preferences("VerseViewer") {
                                put("db_location", locations.first().absolutePath)
                                databasePathProperty.value = locations.first().absolutePath
                            }
                            fire(RefreshUsers())
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
                prefHeight = 125.0
                prefWidth = 250.0

                field("Name") {
                    textfield(note) {
                        required()
                        validator { if (it in controller.userList.map { user -> user.name }) error("$it is already used") else null }
                        whenDocked { requestFocus() }
                    }
                }
                buttonbar {
                    button("Create").action {
                        model.commit {
                            controller.createNewUser(note.value.toString())
                            fire(RefreshUsers())
                            this@dialog.close()
                        }
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