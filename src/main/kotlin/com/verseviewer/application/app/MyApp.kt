package com.verseviewer.application.app

import com.verseviewer.application.view.main.MainView
import javafx.stage.Stage
import tornadofx.*

class MyApp: App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        with(stage) {
            minWidth = 720.0
            minHeight = 480.0
            super.start(this)
        }
    }
}