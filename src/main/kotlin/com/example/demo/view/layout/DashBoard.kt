package com.example.demo.view.layout

import eu.hansolo.tilesfx.Tile
import eu.hansolo.tilesfx.TileBuilder
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.input.KeyCode
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import tornadofx.*

class DashBoard : View("My View") {

    private val testTile = TileBuilder.create().prefSize(25.toDouble(), 25.toDouble()).skinType(Tile.SkinType.CLOCK).build()
    private val dim = 16
    private val tileSize = 25.toDouble()

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



        add(testTile, 2, 2, 4, 4)
        testTile.properties.forEach {
            println("${it.key} : ${it.value}")
        }


    }
}

