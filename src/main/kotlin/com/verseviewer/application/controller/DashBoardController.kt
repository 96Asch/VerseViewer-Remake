package com.verseviewer.application.controller

import com.verseviewer.application.app.Styles
import com.verseviewer.application.model.*
import com.verseviewer.application.model.event.PlaceTile
import com.verseviewer.application.view.dashboard.DashBoard
import com.verseviewer.application.view.dashboard.GridCell
import com.verseviewer.application.model.datastructure.Dimension
import com.verseviewer.application.model.event.HighlightCells
import eu.hansolo.tilesfx.Tile
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import tornadofx.*

class DashBoardController : Controller() {

    private val tileList = mutableListOf<TileProperties>()
    private val builder : ComponentBuilder by inject()
    private val tileModel : TilePropertiesModel by inject()
    private val snapshotModel : SnapshotModel by inject(FX.defaultScope)
    private val view : DashBoard by inject()

    val inEditorProperty = SimpleBooleanProperty(false)
    var inEditor by inEditorProperty

    private val allowedStyle = Styles.placementAllowed
    private val notAllowedStyle = Styles.placementNotAllowed

    fun pickCell(evt : MouseEvent) = view.root.children.firstOrNull {
            val mPt = it.sceneToLocal(evt.sceneX, evt.sceneY)
            it.contains(mPt) && it is GridCell
        } as? GridCell

    fun isPointOnDashboard(point : Point2D) = view.root.contains(point)

    private fun dimensionIntersects(tileDimension: Dimension) : Boolean {
        val list = if (tileModel.item != null) tileList.filterNot { tileModel.item == it } else tileList
        return list.any { tileDimension intersects Dimension(it.x, it.y, it.colspan, it.rowspan) }
    }

    fun validateDrop(cell : GridCell, colspan: Int, rowspan: Int) : Dimension? {
        val (x, y) = getDropCoordinate(cell, colspan, rowspan)
        return validateDrop(x, y, colspan, rowspan)
    }

    fun validateDrop(x : Int, y : Int, colspan: Int, rowspan: Int) : Dimension? {
//        val (boundsX, boundsY) = Pair(x + colspan, y + rowspan)
//        val placementCells = view.root.children
//                .filterIsInstance<GridCell>()
//                .filter { it.x in x until boundsX
//                        && it.y in y until boundsY  }
        val dimension = Dimension(x, y, colspan, rowspan)
        return if (!dimensionIntersects(dimension)) {
//            highlightCells(allowedStyle, placementCells)
            fire(HighlightCells(true, dimension))
            dimension
        }
        else {
            fire(HighlightCells(false, dimension))
//            highlightCells(notAllowedStyle, placementCells)
            null
        }
    }

    fun highlightCells(style : CssRule, x : Int, y : Int, width: Int, height: Int) {
        val (boundsX, boundsY) = Pair(x + width, y + height)
        val placementCells = view.root.children
                .filter { cell -> cell is GridCell
                        && x <= cell.x && cell.x < boundsX
                        && y <= cell.y && cell.y < boundsY  }
        highlightCells(style, placementCells)
    }

    private fun highlightCells(style : CssRule, cells : List<Node>) {
        clearHighlighted()
        cells.forEach {
            if (!it.hasClass(style)) {
                it.addClass(style)
            }
        }
    }

    fun clearHighlighted() {
        view.root.children
                .forEach {
                    if (it.hasClass(allowedStyle))
                        it.removeClass(allowedStyle)
                    if (it.hasClass(notAllowedStyle))
                        it.removeClass(notAllowedStyle)
                }
    }

    fun dropOnGrid(action: EditAction, dropDimension: Dimension, selectedTile: Tile?) : Boolean{
        clearHighlighted()
        var success = false
        when (action) {
            EditAction.NEW_DRAG_DROP -> {
                selectedTile?.let {
                    val tile = builder.createTile(it, isEditable = true, activeInstance = true)
                    addProperty(tile, dropDimension.x, dropDimension.y, dropDimension.width, dropDimension.height)
                    getProperty(tile)
                }
            }

            EditAction.RELOCATE_DRAG_DROP -> {
                selectedTile?.let { view.root.children.remove(it) }
                tileModel.setCoordinate(dropDimension.x, dropDimension.y)
                tileModel.item
            }

            EditAction.RESIZE -> {
                selectedTile?.let { view.root.children.remove(it) }
                tileModel.setSpans(dropDimension.width, dropDimension.height)
                tileModel.item
            }

            else -> null
        }?.apply {
            tile.isVisible = true
            fire(PlaceTile(this))
            success = true
        }
        return success
    }

    private fun getDropCoordinate(cell: GridCell, colspan : Int, rowspan: Int) : Pair<Int, Int> {
        var x = cell.x
        var y = cell.y

        if (x > view.widthTiles - colspan)
            x = view.widthTiles - colspan
        if (y > view.heightTiles - rowspan)
            y = view.heightTiles - rowspan

        return Pair(x,y)
    }


    private fun addProperty(tile : Tile, x : Int, y : Int, colspan : Int, rowspan : Int) {
        val listTile = tileList.firstOrNull { it.tile == tile }
        if (listTile == null)
            tileList.add(TileProperties(tile, x, y, colspan, rowspan))
    }

    private fun getProperty(tile: Tile) : TileProperties?{
        return tileList.firstOrNull { it.tile == tile }
    }

    fun bindProperty(tile : Tile) {
        getProperty(tile)?.apply { tileModel.item = this }
        println(tileModel.itemProperty.isBound)
    }

    private fun removeProperty(tile : Tile) {
        getProperty(tile)?.apply {
            println(tileList.remove(this))
            tile.graphic.removeFromParent()
            tileModel.item = null
        }
    }

    private fun build(gridBuilder: GridBuilder, builder: ComponentBuilder) : List<TileProperties> {
        return gridBuilder.tiles.map {
            buildTileProperties(it, builder)
        }
    }

    private fun buildTileProperties (tileBuilder : TileBuilder, builder: ComponentBuilder) : TileProperties {
        val tile = builder.createTile(tileBuilder.componentId, inEditor)
        return TileProperties(tile, tileBuilder.x, tileBuilder.y, tileBuilder.colspan, tileBuilder.rowspan)
    }

    fun commitTiles() {
        val tileBuilders = tileList.map { TileBuilder(builder.getId(it.tile), it.x, it.y, it.colspan, it.rowspan) }
        snapshotModel.layout.tiles.setAll(tileBuilders)
    }

    fun clearTiles() {
        tileList.clear()
        builder.refreshCounters()
        view.refreshTiles()
    }

    fun getTiles() = tileList.toList()

    fun removeTile(tile: Tile) {
        println(view.root.children.remove(tile))
        builder.increaseInstances(tile)
        removeProperty(tile)
    }

    fun refreshTiles() {
        val tiles = snapshotModel.layout.tiles
        if (tiles.isNotEmpty()) {
            builder.refreshCounters()
            tileList.clear()
            tiles.forEach { tileList.add(buildTileProperties(it, builder)) }
        }
    }

    fun initGrid() {
        tileList.clear()
        tileList.addAll(build(snapshotModel.layout, builder))
    }
}