package com.verseviewer.application.app

import com.verseviewer.application.view.main.Login
import javafx.scene.Scene
import javafx.stage.Stage
import tornadofx.*

class MyApp: App(Login::class, Styles::class) {
    override fun start(stage: Stage) {
        with (stage) {
            minWidth = 720.0
            minHeight = 480.0
            super.start(this)
        }
    }
}