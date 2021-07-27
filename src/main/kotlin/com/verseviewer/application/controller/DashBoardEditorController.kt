package com.verseviewer.application.controller

import com.verseviewer.application.app.Styles
import com.verseviewer.application.model.TilePropertiesModel
import com.verseviewer.application.model.UserModel
import com.verseviewer.application.view.dashboard.DashBoardEditor
import com.verseviewer.application.view.dashboard.DndSkin
import eu.hansolo.tilesfx.Tile
import eu.hansolo.tilesfx.TileBuilder
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import tornadofx.*

class DashBoardEditorController : Controller() {

    val componentList = mutableListOf<Tile>()

    private val builder : ComponentBuilder by inject()
    private val view : DashBoardEditor by inject()
    private val dashboardController : DashBoardController by inject()
    private val tileModel : TilePropertiesModel by inject()

    private val dbController : DBController by inject(FX.defaultScope)
    private val userModel : UserModel by inject(FX.defaultScope)

    private var inFlightTile: Tile? = null
    private var selectedTile: Tile? = null
    private var dropDimension : Dimension? = null
    private var action = EditAction.NONE
    private val minColSpan = 4
    private val minRowSpan = 4

    val dirtyProperty = SimpleBooleanProperty(false)
    var dirty by dirtyProperty
    val requiredComponentsUsedProperty = SimpleBooleanProperty(false)
    var requiredComponentsUsed by requiredComponentsUsedProperty

    fun updateGridToDB() {
        dashboardController.commitTiles()
        userModel.commit()
        dbController.updateLayout(userModel.item)
        dirty = false
    }

    fun startDrag(evt : MouseEvent) {
        val target = evt.target as Node
        val tile = target.findParentOfType(Tile::class)
        val mousePt = view.root.sceneToLocal(evt.sceneX, evt.sceneY)

        tile?.let {
            dropDimension = null
            tileModel.item = null

            if (!dashboardController.isPointOnDashboard(mousePt) && builder.isInstanceAllowed(tile)) {
                setInFlight(tile)
                selectedTile = null
                action = EditAction.NEW_DRAG_DROP
            } else if (dashboardController.isPointOnDashboard(mousePt) && tile.skin is DndSkin) {

                action = getAction(evt, tile.skin as DndSkin, tile)
                when (action) {
                    EditAction.REMOVE -> {
                        dashboardController.removeTile(tile)
                        requiredComponentsUsed = builder.areRequiredComponentsUsed()
                        dirty = true
                    }

                    EditAction.RELOCATE_DRAG_DROP,
                    EditAction.RESIZE -> {
                        selectedTile = tile
                        setInFlight(tile)
                        dashboardController.bindProperty(tile)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setInFlight(tile : Tile) {
        inFlightTile = builder.createTile(tile)
                .apply { addClass(Styles.partialTransparant)
                    relocate(tile.layoutX, tile.layoutY)
                    isVisible = false }
        view.root.children.last().add(inFlightTile!!)
    }

    fun animateDrag(evt : MouseEvent) {
        if (action != EditAction.NONE) {
            selectedTile?.apply { if (isVisible) isVisible = false }
            val mousePt = view.root.sceneToLocal(evt.sceneX, evt.sceneY)
            inFlightTile?.apply {
                isVisible = true
                toFront()
            }

            val cell = dashboardController.pickCell(evt)

            when(action) {
                EditAction.NEW_DRAG_DROP -> {
                    inFlightTile?.relocate(mousePt.x, mousePt.y)
                    cell?.let {
                        dropDimension = dashboardController.validateDrop(it, minColSpan, minRowSpan)
                    }
                }

                EditAction.RELOCATE_DRAG_DROP -> {
                    inFlightTile?.relocate(mousePt.x, mousePt.y)
                    cell?.let {
                        dropDimension = dashboardController.validateDrop(it, tileModel.colspan.value, tileModel.rowspan.value)
                    }
                }

                EditAction.RESIZE -> {
                    inFlightTile?.setPrefSize(mousePt.x - selectedTile!!.layoutX, mousePt.y - selectedTile!!.layoutY)
                    cell?.let {
                        val cspan = it.x - tileModel.x.value + 1
                        val rspan = it.y - tileModel.y.value + 1
                        val colspan = if (cspan > minColSpan) cspan else minColSpan
                        val rowspan = if (rspan > minRowSpan) rspan else minRowSpan
                        dropDimension = dashboardController.validateDrop(tileModel.x.value, tileModel.y.value, colspan, rowspan)
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

    fun stopDrag() {
        if (action != EditAction.NONE && inFlightTile != null) {
            inFlightTile!!.isVisible = false
        }
    }

    fun drop(evt: MouseEvent) {
        val mousePt = view.root.sceneToLocal(evt.sceneX, evt.sceneY)

        if (dashboardController.isPointOnDashboard(mousePt) && dropDimension != null) {
            when (action) {
                EditAction.NEW_DRAG_DROP -> {
                    if (dashboardController.dropOnGrid(action, dropDimension!!, inFlightTile)) {
                        requiredComponentsUsed = builder.areRequiredComponentsUsed()
                        dirty = true
                        evt.consume()
                    }
                }

                EditAction.RELOCATE_DRAG_DROP,
                EditAction.RESIZE -> {
                    if (dashboardController.dropOnGrid(action, dropDimension!!, selectedTile)) {
                        dirty = true
                        selectedTile = null
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
        inFlightTile?.graphic?.removeFromParent()
        inFlightTile = null
        action = EditAction.NONE
    }

    private fun getAction(evt: MouseEvent, skin: DndSkin, tile : Tile): EditAction {
        return when {
            isMouseInRegion(skin.relocateRegion, evt) -> EditAction.RELOCATE_DRAG_DROP
            isMouseInRegion(skin.closeRegion, evt) -> EditAction.REMOVE
            isMouseInRegion(skin.resizeRegion, evt) -> EditAction.RESIZE
            else -> EditAction.NONE
        }
    }

    private fun isMouseInRegion(region : Region, evt: MouseEvent) : Boolean
            = region.contains(region.screenToLocal(evt.screenX, evt.screenY))

    private fun refreshComponents() {
        componentList.clear()
        componentList.addAll(builder.createListComponents())
        requiredComponentsUsed = builder.areRequiredComponentsUsed()
    }

    init {
        refreshComponents()
    }
}

data class Dimension(val x : Int, val y: Int, val width: Int, val height: Int) {
    infix fun intersects(other: Dimension) : Boolean {
        val x2 = x + width
        val y2 = y + height
        val otherX2 = other.x + other.width
        val otherY2 = other.y + other.height
        return (x < otherX2 && x2 > other.x && y < otherY2 && y2 > other.y)
    }
}

enum class EditAction {
    NONE,
    REMOVE,
    NEW_DRAG_DROP,
    RELOCATE_DRAG_DROP,
    RESIZE
}