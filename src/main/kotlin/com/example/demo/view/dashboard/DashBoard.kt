package com.example.demo.view.dashboard

import com.example.demo.controller.DashBoardController
import com.example.demo.model.TileProperties
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.layout.*
import tornadofx.*

class DashBoard : View("My View") {
    val numTiles = 16
    val tileSize = 25.0
    private val controller : DashBoardController by inject()

    override val root = gridpane {
        isGridLinesVisible = true

        for (i in 0 until numTiles) {
            val c = ColumnConstraints().apply {
                halignment = HPos.CENTER
                hgrow = Priority.ALWAYS
                this.prefWidth = tileSize
            }
            columnConstraints.add(c)
        }

        for (i in 0 until numTiles) {
            val r = RowConstraints().apply {
                valignment = VPos.CENTER
                vgrow = Priority.ALWAYS
                this.prefHeight = tileSize
            }
            rowConstraints.add(r)
        }

        for (y in 0 until numTiles) {
            for (x in 0 until numTiles) {
                val pane = GridCell(x,y)
                add(pane, x, y)
            }
        }
    }

    fun addTiles() {
        controller.tileList.forEach {
            root.addTile(it)
        }
    }

    init {
        addTiles()
    }
}

fun GridPane.addTile(property : TileProperties) {
    add(property.tile, property.x, property.y, property.colspan, property.rowspan)
}

class GridCell(val x : Int, val y : Int) : Pane()

