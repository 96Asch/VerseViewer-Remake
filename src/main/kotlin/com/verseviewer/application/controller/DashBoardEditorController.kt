package com.verseviewer.application.controller

import com.verseviewer.application.app.Styles
import com.verseviewer.application.model.SnapshotModel
import com.verseviewer.application.model.TilePropertiesModel
import com.verseviewer.application.model.datastructure.Dimension
import com.verseviewer.application.model.event.ClearHighlights
import com.verseviewer.application.model.event.HighlightCells
import com.verseviewer.application.model.event.PlaceInFlightTile
import com.verseviewer.application.view.dashboard.DashBoardEditor
import com.verseviewer.application.view.dashboard.DndSkin
import eu.hansolo.tilesfx.Tile
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import tornadofx.*

enum class EditAction {
    NONE,
    REMOVE,
    NEW_DRAG_DROP,
    RELOCATE_DRAG_DROP,
    RESIZE
}

class DashBoardEditorController : Controller() {

    val componentList = mutableListOf<Tile>()

    private val builder : ComponentBuilder by inject()

    private val tileModel : TilePropertiesModel by inject()
    private val snapshotModel : SnapshotModel by inject(FX.defaultScope)

    private val dashboardController : DashBoardController by inject()
    private val dbController : DBController by inject(FX.defaultScope)

    private var inFlightTile: Tile? = null
    private var selectedTile: Tile? = null
    private var dropDimension : Dimension? = null
    private var action = EditAction.NONE
    private val minColSpan = 6
    private val minRowSpan = 6

    val dirtyProperty = SimpleBooleanProperty(false)
    var dirty by dirtyProperty
    val requiredComponentsUsedProperty = SimpleBooleanProperty(false)
    var requiredComponentsUsed by requiredComponentsUsedProperty

    fun saveDashboard() {
        dashboardController.commitTiles()
        dbController.updateLayout(snapshotModel.item)
        dirty = false
    }

    fun startDrag(evt : MouseEvent, mousePt : Point2D) {
        val target = evt.target as Node
        val tile = target.findParentOfType(Tile::class)

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
                .apply {
                    addClass(Styles.partialTransparant)
                    relocate(tile.layoutX, tile.layoutY)
                    isVisible = false
                    fire(PlaceInFlightTile(this))
                }
    }

    fun animateDrag(evt : MouseEvent, mousePt: Point2D) {
        if (action != EditAction.NONE) {
            selectedTile?.apply { if (isVisible) isVisible = false }
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
                    fire(HighlightCells(false, Dimension(it.x, it.y, it.width, it.height)))
                }
            }
        }
    }

    fun stopDrag() {
        if (action != EditAction.NONE && inFlightTile != null) {
            inFlightTile!!.isVisible = false
        }
    }

    fun drop(evt: MouseEvent, mousePt : Point2D) {
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
            fire(ClearHighlights())
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

    init {
        componentList.clear()
        componentList.addAll(builder.createListComponents())
        requiredComponentsUsed = builder.areRequiredComponentsUsed()
    }
}