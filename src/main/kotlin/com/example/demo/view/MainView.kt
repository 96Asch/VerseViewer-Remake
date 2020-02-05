package com.example.demo.view

import com.example.demo.view.schedule.Schedule
import com.example.demo.view.versebox.VerseBox
import tornadofx.*

class MainView : View("Hello TornadoFX") {
    override val root = vbox {
        this += find<VerseBox>()
        this += find<Schedule>()
    }
}