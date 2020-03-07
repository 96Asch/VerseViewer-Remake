package com.example.demo.controller

import com.example.demo.app.Styles
import com.example.demo.model.TilePropertiesModel
import com.example.demo.view.dashboard.*
import eu.hansolo.tilesfx.Tile
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.geometry.BoundingBox
import javafx.scene.Node
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import tornadofx.*
import kotlin.math.E

class DashBoardEditorController : Controller() {

    val componentList = mutableListOf<Tile>()

    private val builder : ComponentBuilder by inject()
    private val view : DashBoardEditor by inject()
    private val dashboardController : DashBoardController by inject()
    private val dashboardView : DashBoard by inject()
    private val tileModel : TilePropertiesModel by inject()

    private lateinit var inFlightTile: Tile
    private var selectedTile: Tile? = null
    private var dropDimension : Dimension? = null
    private var action = EditAction.NONE
    private val minSpan = 3

    val dirtyProperty = SimpleBooleanProperty(false)


    fun startDrag(evt : MouseEvent) {
        val target = evt.target as Node
        val tile = target.findParentOfType(Tile::class)
        val mousePt = view.root.sceneToLocal(evt.sceneX, evt.sceneY)

        tile?.let {
            if (!dashboardView.root.contains(mousePt)) {
                setInFlight(tile)
                selectedTile = null
                action = EditAction.NEW_DRAG_DROP
            } else if (dashboardView.root.contains(mousePt) && tile.skin is DndSkin) {
                selectedTile = tile
                action = getAction(evt, tile.skin as DndSkin)
                when (action) {
                    EditAction.NONE -> {}

                    EditAction.REMOVE -> {
                        dashboardController.removeTile(tile)
                        dirtyProperty.value = true
                    }

                    EditAction.NEW_DRAG_DROP,
                    EditAction.RELOCATE_DRAG_DROP,
                    EditAction.RESIZE -> {
                        setInFlight(tile)
                        dashboardController.clearHighlighted()
                        dashboardController.bindProperty(tile)
                    }
                }
            }
        }
    }

    private fun setInFlight(tile : Tile) {
        println("${tile.layoutY}, ${tile.layoutX}")
        inFlightTile = builder.createTile(tile, isEditable = false)
                .apply { addClass(Styles.partialTransparant)
                    setPrefSize(tile.width, tile.height)
                    relocate(tile.layoutX, tile.layoutY)
                    isVisible = false }
        view.root.children.last().add(inFlightTile)
    }

    fun animateDrag(evt : MouseEvent) {
        if (action != EditAction.NONE) {
            selectedTile?.apply { if (isVisible) isVisible = false }
            val mousePt = view.root.sceneToLocal(evt.sceneX, evt.sceneY)
            inFlightTile.isVisible = true
            inFlightTile.toFront()

            val cell = dashboardView.root.children.firstOrNull {
                val mPt = it.sceneToLocal(evt.sceneX, evt.sceneY)
                it.contains(mPt) && it is GridCell
            } as? GridCell

            when(action) {
                EditAction.NEW_DRAG_DROP -> {
                    inFlightTile.relocate(mousePt.x, mousePt.y)
                    cell?.let {
                        dropDimension = dashboardController.validatePlacementCells(it, minSpan, minSpan)
                    }
                }

                EditAction.RELOCATE_DRAG_DROP -> {
                    inFlightTile.relocate(mousePt.x, mousePt.y)
                    cell?.let {
                        dropDimension = dashboardController.validatePlacementCells(it, tileModel.colspan.value, tileModel.rowspan.value)
                    }
                }

                EditAction.RESIZE -> {
                    inFlightTile.setPrefSize(mousePt.x - selectedTile!!.layoutX, mousePt.y - selectedTile!!.layoutY)
                    cell?.let {
                        val colspan = it.x - tileModel.x.value + 1
                        val rowspan = it.y - tileModel.y.value + 1
                        dropDimension = dashboardController.validatePlacementCells(tileModel.x.value, tileModel.y.value, colspan, rowspan)
                    }
                }
                else -> {}
            }
            if (cell == null) {
                dropDimension?.let {
                    dashboardController.highlightCells(Styles.placementNotAllowed, it.x, it.y, it.width, it.height)
                }
            }
        }
    }

    fun stopDrag(evt: MouseEvent) {
        if (action != EditAction.NONE) {
            inFlightTile.isVisible = false
        }
    }

    fun drop(evt: MouseEvent) {
        val mousePt = view.root.sceneToLocal(evt.sceneX, evt.sceneY)

        if (dashboardView.root.contains(mousePt) && dropDimension != null) {
            when (action) {
                EditAction.NEW_DRAG_DROP -> {
                    if (dashboardController.dropOnGrid(action, dropDimension!!, inFlightTile)) {
                        dirtyProperty.value = true
                        evt.consume()
                    }
                }
                EditAction.RELOCATE_DRAG_DROP,
                EditAction.RESIZE-> {
                    if (dashboardController.dropOnGrid(action, dropDimension!!, selectedTile)) {
                        dirtyProperty.value = true
                        evt.consume()
                    }
                }

                else -> {}
            }
        }
        else {
            selectedTile?.apply { isVisible = true }
            dashboardController.clearHighlighted()
        }
        action = EditAction.NONE
    }


    private fun getAction(evt: MouseEvent, skin: DndSkin): EditAction {
        return when {
            isMouseInRegion(skin.relocateRegion, evt) -> EditAction.RELOCATE_DRAG_DROP
            isMouseInRegion(skin.closeRegion, evt) -> EditAction.REMOVE
            isMouseInRegion(skin.resizeRegion, evt) ->  EditAction.RESIZE
            else -> EditAction.NONE
        }
    }

    private fun isMouseInRegion(region : Region, evt: MouseEvent) : Boolean
            = region.contains(region.sceneToLocal(evt.x, evt.y))


    private fun refreshComponents() {
        val restList = builder.components
                .map { builder.createTile(it.value, it.value.classType.simpleName!!, isEditable = false).apply {
                    onMouseEntered = EventHandler { scene.cursor = Cursor.OPEN_HAND }
                    onMouseExited = EventHandler { scene.cursor = Cursor.DEFAULT }
                } }
        componentList.clear()
        componentList.addAll(restList)
    }

    init {
        refreshComponents()
    }
}

data class Dimension(val x : Int, val y: Int, val width: Int, val height: Int)

enum class EditAction {
    NONE,
    REMOVE,
    NEW_DRAG_DROP,
    RELOCATE_DRAG_DROP,
    RESIZE
}