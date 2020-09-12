package com.verseviewer.application.view.dashboard

import eu.hansolo.tilesfx.Tile
import eu.hansolo.tilesfx.skins.CustomTileSkin
import eu.hansolo.tilesfx.tools.Helper
import javafx.scene.control.Label

class ListSkin(tile: Tile, private val regionSize : Double) : CustomTileSkin(tile) {

    lateinit var counterRegion : CounterRegion

    override fun initGraphics() {
        super.initGraphics()
        counterRegion = CounterRegion()

        Helper.enableNode(counterRegion, true)

        pane.children.addAll(counterRegion)
        counterRegion.toFront()
    }

    override fun resize() {
        super.resize()
        counterRegion.setPrefSize(regionSize, regionSize)
        counterRegion.relocate(width - regionSize, height - regionSize)
    }

    override fun redraw() {
        super.redraw()
        counterRegion.roundedCorner = tile.roundedCorners
    }
}