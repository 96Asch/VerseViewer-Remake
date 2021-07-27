package com.verseviewer.application.view.dashboard

import com.verseviewer.application.controller.DashBoardController
import com.verseviewer.application.model.TileProperties
import eu.hansolo.tilesfx.Tile
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.layout.*
import tornadofx.*

class DashBoard : View() {
    val heightTiles = 16
    val widthTiles = 32
    val tileSize = 25.0
    private var inEditor = false

    private val controller : DashBoardController by inject()

    override val root = gridpane {
        paddingAll = 5.0
        vgap = 5.0
        hgap = 5.0
        for (i in 0 until widthTiles) {
            val c = ColumnConstraints().apply {
                halignment = HPos.CENTER
                hgrow = Priority.ALWAYS
                this.prefWidth = tileSize
            }
            columnConstraints.add(c)
        }

        for (i in 0 until heightTiles) {
            val r = RowConstraints().apply {
                valignment = VPos.CENTER
                vgrow = Priority.ALWAYS
                this.prefHeight = tileSize
            }
            rowConstraints.add(r)
        }

        for (y in 0 until heightTiles) {
            for (x in 0 until widthTiles) {
                val pane = GridCell(x,y)
                add(pane, x, y)
            }
        }
    }

    fun refreshTiles() {
        if (inEditor)
            root.children.removeIf { it is Tile }
        else
            root.children.clear()
        controller.getTiles().forEach {
            root.addTile(it)
        }
    }

    override fun onDock() {
        inEditor = params["inEditor"] as? Boolean ?: false
        root.isGridLinesVisible = inEditor
        controller.initGrid(inEditor)
        refreshTiles()
    }
}


fun GridPane.addTile(property : TileProperties) {
    add(property.tile, property.x, property.y, property.colspan, property.rowspan)
}

class GridCell(val x : Int, val y : Int) : Pane()

