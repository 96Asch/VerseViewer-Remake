package com.example.demo.controller

import com.example.demo.view.dashboard.DndSkin
import com.example.demo.view.versebox.VerseBox
import eu.hansolo.tilesfx.Tile
import eu.hansolo.tilesfx.TileBuilder
import javafx.scene.paint.Color
import tornadofx.*

class TileBuilderController : Controller() {

    fun createDndTile() : Tile{
        val testTile = TileBuilder.create()
                .prefSize(25.toDouble(), 25.toDouble())
                .skinType(Tile.SkinType.CUSTOM)
                .roundedCorners(false)
                .graphic(find(VerseBox::class).root)
                .infoRegionBackgroundColor(Color.RED)
                .notifyRegionBackgroundColor(Color.YELLOW)
                .build()

        val dndSkin = DndSkin(testTile)
        dndSkin.resizeRegion.backgroundColor = Tile.GRAY
        dndSkin.resizeRegion.foregroundColor = Tile.DARK_BLUE
        dndSkin.closeRegion.backgroundColor = Tile.RED
        testTile.skin = dndSkin
        testTile.isTextVisible = false
        testTile.showNotifyRegion(true)

        return testTile
    }
}