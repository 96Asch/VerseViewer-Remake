package com.example.demo.view

import com.example.demo.view.schedule.Schedule
import com.example.demo.view.versebox.VerseBox
import tornadofx.*

class MainView : View("Hello TornadoFX") {
    override val root = gridpane {
        isGridLinesVisible = true
        row {
            this += find<VerseBox>() {
                gridpaneConstraints {
                    fillHeightWidth = true
                    columnRowIndex(0, 0)
                }
            }
            this += find<Schedule>() {
                gridpaneConstraints {
                    fillHeightWidth = true
                    columnRowIndex(0, 1)
                }
            }
        }
    }
}