package com.verseviewer.application.view.schedule

import javafx.scene.Parent
import tornadofx.*

class ScheduleMenu : View() {

    override val root = pane {
        opacity = 0.0
        button("Group") {  }
        button("UnGroup") {  }
        button("Delete") {  }

    }
}