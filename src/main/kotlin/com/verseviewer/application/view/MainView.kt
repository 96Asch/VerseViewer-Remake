package com.verseviewer.application.view

import com.verseviewer.application.model.scope.ScheduleScope
import com.verseviewer.application.view.schedule.Schedule
import com.verseviewer.application.view.versebox.VerseBox
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import tornadofx.*

class MainView : View("Hello TornadoFX") {

    private val dim = 16
    private val tileSize = 50.toDouble()
    override val root = gridpane {
        isGridLinesVisible = true

        for (i in 0 until dim) {
            val c = ColumnConstraints().apply {
                halignment = HPos.CENTER
                hgrow = Priority.ALWAYS
                this.prefWidth = tileSize
            }
            columnConstraints.add(c)
        }

        for (i in 0 until dim) {
            val r = RowConstraints().apply {
                valignment = VPos.CENTER
                vgrow = Priority.ALWAYS
                this.prefHeight = tileSize
            }
            rowConstraints.add(r)
        }

        add(find(VerseBox::class).root, 0,0, 10,10)

        add(find(Schedule::class, ScheduleScope()).root, 0, 11, 7 , 7)
        add(find(Schedule::class, ScheduleScope()).root, 10, 11, 10 , 6)

    }
}