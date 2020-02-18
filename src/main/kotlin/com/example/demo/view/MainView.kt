package com.example.demo.view

import com.example.demo.model.ScheduleScope
import com.example.demo.view.schedule.Schedule
import com.example.demo.view.versebox.VerseBox
import tornadofx.*

class MainView : View("Hello TornadoFX") {

    val map = mapOf("test" to VerseBox::class, "t" to Schedule::class)!!

    override val root = gridpane {
        isGridLinesVisible = true

            this += find(map["test"]?: VerseBox::class). apply {
                    gridpaneConstraints {
                        fillHeightWidth = true
                        columnRowIndex(0, 0)
            }

            this += find(Schedule::class, ScheduleScope()).apply {
                gridpaneConstraints {
                    fillHeightWidth = true
                    columnRowIndex(0, 1)
                }
            }
        }
    }
}