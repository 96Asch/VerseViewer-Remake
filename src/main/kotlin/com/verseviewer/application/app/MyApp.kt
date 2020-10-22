package com.verseviewer.application.app

import com.verseviewer.application.view.main.Login
import com.verseviewer.application.view.main.MainView
import tornadofx.*

class MyApp: App(Login::class, Styles::class) {

//    override fun createPrimaryScene(view: UIComponent): Scene {
//        return BorderlessScene(view.currentStage, StageStyle.UTILITY, view.root)
//    }
}