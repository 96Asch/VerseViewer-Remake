package com.example.demo.view.dashboard

import com.example.demo.controller.TileBuilderController
import com.example.demo.model.DashBoardModel
import com.example.demo.model.GridBuilder
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import tornadofx.*

class DashBoard : View("My View") {

    private val dashboardModel : DashBoardModel by inject()
    private val json = resources.jsonArray("/layout/gridinfo.json").toModel<GridBuilder>()
    private val dim = 16
    private val tileSize = 30.0

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
    }

    init {
        dashboardModel.build(json, 1, true)
        dashboardModel.item.tiles.forEach {
            root.add(it.tile, it.x, it.y, it.colspan, it.rowspan)
        }
    }
}

