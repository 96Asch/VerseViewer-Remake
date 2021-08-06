package com.verseviewer.application.view.dashboard

import com.verseviewer.application.controller.DashBoardController
import com.verseviewer.application.model.TileProperties
import com.verseviewer.application.model.event.PlaceTile
import eu.hansolo.tilesfx.Tile
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.layout.*
import tornadofx.*

class GridCell(val x : Int, val y : Int) : Pane()

fun GridPane.addTiles(tiles : List<TileProperties>) {
    tiles.forEach { add(it.tile, it.x, it.y, it.colspan, it.rowspan) }
}

class DashBoard : View() {
    val heightTiles = 16
    val widthTiles = 32
    val tileSize = 25.0

    private val controller : DashBoardController by inject()

    override val root = gridpane {

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
                add(GridCell(x, y), x, y)
            }
        }

        paddingAll = 2.5
        gridLinesVisibleProperty().bind(controller.inEditorProperty)
    }

    fun refreshTiles() {
        if (controller.inEditor)
            root.children.removeIf { it is Tile }
        else
            root.children.clear()

        root.addTiles(controller.getTiles())
    }

    override fun onDock() {
        controller.inEditor = params["inEditor"] as? Boolean ?: false
        controller.initGrid()
        refreshTiles()
    }

    init {
        subscribe<PlaceTile> {
            root.add(it.tileProperty.tile,
                    it.tileProperty.x,
                    it.tileProperty.y,
                    it.tileProperty.colspan,
                    it.tileProperty.rowspan)
        }
    }
}

