package com.example.demo.view.dashboard

import eu.hansolo.tilesfx.Tile
import eu.hansolo.tilesfx.skins.CustomTileSkin
import eu.hansolo.tilesfx.tools.Helper


open class DndSkin(TILE: Tile, size : Double) : CustomTileSkin(TILE) {

    lateinit var resizeRegion: ResizeRegion
    lateinit var closeRegion: CloseRegion
    val regionSize = size

    override fun initGraphics() {
        super.initGraphics()
        resizeRegion = ResizeRegion()
        closeRegion = CloseRegion()

        Helper.enableNode(resizeRegion, true)
        Helper.enableNode(closeRegion, true)

        pane.children.addAll(resizeRegion, closeRegion)

        resizeRegion.toFront()
        closeRegion.toFront()
    }

    override fun resize() {
        super.resize()


        println(size)
        resizeRegion.setPrefSize(regionSize, regionSize)
        resizeRegion.relocate(width - regionSize, height - regionSize)

        closeRegion.setPrefSize(regionSize, regionSize)
        closeRegion.relocate(0.0, 0.0)
    }

    override fun redraw() {
        super.redraw()
        resizeRegion.roundedCorner = tile.roundedCorners
        closeRegion.roundedCorner = tile.roundedCorners
    }

}

