package com.example.demo.controller

import com.example.demo.app.Styles
import com.example.demo.model.*
import com.example.demo.view.dashboard.DashBoard
import com.example.demo.view.dashboard.GridCell
import com.example.demo.view.dashboard.addTile
import eu.hansolo.tilesfx.Tile
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import tornadofx.*
import javax.json.JsonArray

class DashBoardController : Controller() {

    val tileList = mutableListOf<TileProperties>()

    private val builder : ComponentBuilder by inject()
    private val json = resources.jsonArray("/layout/gridinfo.json")
    private val tileModel : TilePropertiesModel by inject()
    private val view : DashBoard by inject()

    fun removeTile(tile: Tile) {
        view.root.children.remove(tile)
        removeProperty(tile)
    }


    fun validatePlacementCells(cell : GridCell, colspan: Int, rowspan: Int) : Dimension? {
        val (x, y) = getDropCoordinate(cell, colspan, rowspan)
        return validatePlacementCells(x, y, colspan, rowspan)
    }

    fun validatePlacementCells(x : Int, y : Int, colspan: Int, rowspan: Int) : Dimension? {
        val (boundsX, boundsY) = Pair(x + colspan, y + rowspan)
        val placementCells = view.root.children
                .filter { it is GridCell
                        && x <= it.x && it.x < boundsX
                        && y <= it.y && it.y < boundsY  }
        return if (placementCells.size == colspan * rowspan) {
            highlightCells(Styles.placementAllowed, placementCells)
            Dimension(x, y, colspan, rowspan)
        }
        else {
            highlightCells(Styles.placementNotAllowed, placementCells)
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
                    if (it.hasClass(Styles.placementAllowed))
                        it.removeClass(Styles.placementAllowed)
                    if (it.hasClass(Styles.placementNotAllowed))
                        it.removeClass(Styles.placementNotAllowed)
                }
    }

    fun dropOnGrid(action: EditAction, dropDimension: Dimension, selectedTile: Tile?) : Boolean{
        clearHighlighted()
        var success = false
        when (action) {
            EditAction.NEW_DRAG_DROP -> {
                selectedTile?.let {
                    val tile = builder.createTile(it, isEditable = true)
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
            view.root.addTile(this)
            success = true
        }
        return success
    }

    private fun getDropCoordinate(cell: GridCell, colspan : Int, rowspan: Int) : Pair<Int, Int> {
        var x = cell.x
        var y = cell.y

        if (x > view.numTiles - colspan)
            x = view.numTiles - colspan
        if (y > view.numTiles - rowspan)
            y = view.numTiles - rowspan

        return Pair(x,y)
    }


    fun addProperty(tile : Tile, x : Int, y : Int, colspan : Int, rowspan : Int) {
        val listTile = tileList.firstOrNull { it.tile == tile }
        if (listTile == null)
            tileList.add(TileProperties(tile, x, y, colspan, rowspan))
    }

    fun getProperty(tile: Tile) : TileProperties?{
        return tileList.firstOrNull { it.tile == tile }
    }

    fun bindProperty(tile : Tile) {
        tileList.firstOrNull { it.tile == tile }?.apply { tileModel.item = this }
    }

    fun removeProperty(tile : Tile) {
        tileList.firstOrNull { it.tile == tile }?.apply { tileList.remove(this) }
    }

    fun loadFromJson(json : JsonArray) {
        val gridBuilder = json.toModel<GridBuilder>()
        build(gridBuilder, builder,1, true)
    }

    fun build(list: List<GridBuilder>, builder : ComponentBuilder, activeDb: Int, isEditable: Boolean = false) {
        tileList.clear()
        list.asSequence().forEach { gb ->
            if (gb.id == activeDb) {
                gb.tiles.forEach{ tileList.add(buildTileProperties(it, builder, isEditable)) }
            }
        }
    }

    private fun buildTileProperties (tileBuilder : TileBuilder, builder: ComponentBuilder, isEditable: Boolean) : TileProperties {
        return TileProperties(builder.createTile(tileBuilder.componentId, isEditable), tileBuilder.x, tileBuilder.y, tileBuilder.colspan, tileBuilder.rowspan)
    }

    fun refreshTiles() {
        loadFromJson(json)
    }

    init {
        tileModel.itemProperty.addListener {_,_,new -> println(new)}
        refreshTiles()
    }



}