package com.example.demo.view.dashboard

import com.example.demo.app.Styles
import eu.hansolo.tilesfx.Tile
import eu.hansolo.tilesfx.skins.CustomTileSkin
import eu.hansolo.tilesfx.tools.Helper
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Group
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import tornadofx.*


open class DndSkin(tile: Tile, private val regionSize : Double) : CustomTileSkin(tile) {

    lateinit var resizeRegion: ResizeRegion
    lateinit var closeRegion: CloseRegion
    lateinit var relocateRegion: RelocateRegion

    override fun initGraphics() {
        super.initGraphics()
        val glyph = GlyphFontRegistry.font("FontAwesome")

        relocateRegion = RelocateRegion(glyph.create(FontAwesome.Glyph.ARROWS)).apply { addCss(Styles.highlightTile) }
        resizeRegion = ResizeRegion(glyph.create(FontAwesome.Glyph.ARROWS_H)).apply { addCss(Styles.highlightTile) }
        closeRegion = CloseRegion(glyph.create(FontAwesome.Glyph.TRASH_ALT)).apply { addCss(Styles.highlightTile) }

        Helper.enableNode(relocateRegion, true)
        Helper.enableNode(resizeRegion, true)
        Helper.enableNode(closeRegion, true)

        relocateRegion.onMouseEntered = EventHandler { tile.scene.cursor = Cursor.OPEN_HAND  }
        relocateRegion.onMousePressed = EventHandler { tile.scene.cursor = Cursor.CLOSED_HAND }
        relocateRegion.onMouseReleased = EventHandler { tile.scene.cursor = Cursor.OPEN_HAND }
        relocateRegion.onMouseExited = EventHandler { tile.scene.cursor = Cursor.DEFAULT }
        resizeRegion.onMouseEntered = EventHandler { tile.scene.cursor = Cursor.SE_RESIZE  }
        resizeRegion.onMouseExited = EventHandler { tile.scene.cursor = Cursor.DEFAULT }

        pane.children.addAll(Group(relocateRegion), Group(resizeRegion), closeRegion)

        relocateRegion.toFront()
        resizeRegion.toFront()
        closeRegion.toFront()
    }

    override fun resize() {
        super.resize()

        relocateRegion.setPrefSize(regionSize, regionSize)
        relocateRegion.relocate(0.0, 0.0)


        resizeRegion.setPrefSize(regionSize, regionSize)
        resizeRegion.relocate(width - regionSize, height - regionSize)
        resizeRegion.icon.rotate = 45.0

        closeRegion.setPrefSize(regionSize, regionSize)
        closeRegion.relocate(width - regionSize, 0.0)
    }

    override fun redraw() {
        super.redraw()
        relocateRegion.roundedCorner = tile.roundedCorners
        resizeRegion.roundedCorner = tile.roundedCorners
        closeRegion.roundedCorner = tile.roundedCorners
    }

}

