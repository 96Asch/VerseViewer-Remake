package com.verseviewer.application.app

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene
import com.verseviewer.application.view.booklist.BookList
import com.verseviewer.application.view.dashboard.DashBoard
import com.verseviewer.application.view.dashboard.DashBoardEditor
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.*

class MyApp: App(DashBoard::class, Styles::class) {

//    override fun createPrimaryScene(view: UIComponent): Scene {
//        return BorderlessScene(view.currentStage, StageStyle.UTILITY, view.root)
//    }
}