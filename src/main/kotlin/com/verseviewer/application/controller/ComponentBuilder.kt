package com.verseviewer.application.controller

import com.verseviewer.application.app.Styles
import com.verseviewer.application.model.scope.EditorScope
import com.verseviewer.application.model.scope.ScheduleScope
import com.verseviewer.application.view.dummy.Dummy
import com.verseviewer.application.view.booklist.BookList
import com.verseviewer.application.view.dashboard.DndSkin
import com.verseviewer.application.view.dashboard.ListSkin
import com.verseviewer.application.view.projection.ProjectionBar
import com.verseviewer.application.view.schedule.Schedule
import com.verseviewer.application.view.versebox.VerseBox
import eu.hansolo.tilesfx.Tile
import eu.hansolo.tilesfx.TileBuilder
import javafx.beans.property.SimpleIntegerProperty
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class ComponentBuilder : Controller() {

    private val componentIdName = "componentId"
    private val tileSize = 25.0
    private val dummyComponent = VVComponent(Dummy::class, maxInstances = -1)
    private val editorScope = EditorScope()
    
    private val components = mapOf(
            ProjectionBar::class.simpleName to VVComponent(ProjectionBar::class, maxInstances = 1, required = true),
            VerseBox::class.simpleName to VVComponent(VerseBox::class, maxInstances = 1, required = true),
            Schedule::class.simpleName to VVComponent(Schedule::class, maxInstances = -1, scope = ScheduleScope::class, required = false),
            BookList::class.simpleName to VVComponent(BookList::class, maxInstances = 1, required = false)
    )

    fun isInstanceAllowed(tile: Tile) : Boolean {
        val component = getComponent(tile)
        return if (component.maxInstances > 0 && component.numInstances > 0)
            true
        else component.maxInstances < 0
    }

    fun removeInstance(tile: Tile) {
        val component = components[tile.properties[componentIdName] as String]
        component?.apply { numInstances++ }
    }

    fun createListComponents() = components.map {
        createListTile(it.value, it.value.classType.simpleName!!)
    }

    
    private fun createListTile(component : VVComponent, id : String) : Tile {
        val listTile = TileBuilder.create()
                .prefSize(tileSize, tileSize)
                .maxHeight(Double.MAX_VALUE)
                .maxWidth(Double.MAX_VALUE)
                .activeColor(Color.GREEN)
                .backgroundColor(Color.WHEAT)
                .skinType(Tile.SkinType.CUSTOM)
                .textVisible(false)
                .roundedCorners(true)
                .showInfoRegion(component.required)
                .infoRegionTooltipText("Component required")
                .graphic(component.getInstance(editorScope = editorScope).root)
                .build()

        listTile.properties[componentIdName] = id

        val listSkin = ListSkin(listTile, tileSize * 1.2)
        listSkin.counterRegion.backgroundColor = Color.GRAY
        if (component.maxInstances < 0) {
            listSkin.counterRegion.counterLabel.text = "∞"
        }
        else {
            listSkin.counterRegion.counterLabel.textProperty().bind(component.numInstancesProperty.asString())
        }
        listTile.skin = listSkin
        tileInstanceHandlers(component, listTile)

        return listTile
    }

    private fun tileInstanceHandlers(component: VVComponent, tile: Tile) {
        tile.onMouseEntered = EventHandler { tile.scene.cursor = Cursor.OPEN_HAND }
        tile.onMouseExited = EventHandler { tile.scene.cursor = Cursor.DEFAULT }

        if (component.numInstances == 0) {
            tile.onMouseEntered = EventHandler { tile.scene.cursor = Cursor.DEFAULT }
            tile.addClass(Styles.greyedOut)
            tile.removeClass(Styles.highlightTile)
        }

        component.numInstancesProperty.addListener { _, oldValue, newValue ->
            if (newValue == 0) {
                tile.onMouseEntered = EventHandler { tile.scene.cursor = Cursor.DEFAULT }
                tile.addClass(Styles.greyedOut)
                tile.removeClass(Styles.highlightTile)
            }
            else if (oldValue == 0) {
                tile.onMouseEntered = EventHandler { tile.scene.cursor = Cursor.OPEN_HAND }
                tile.removeClass(Styles.greyedOut)
                tile.addClass(Styles.highlightTile)
            }
        }
    }

    private fun createTile(component : VVComponent,
                           isEditable: Boolean = false,
                           activeInstance: Boolean = false,
                           width : Double = tileSize,
                           height : Double = tileSize) : Tile {
        val fragment = component.getInstance(activeInstance)
        val tile = TileBuilder.create()
                .prefSize(width, height)
                .maxHeight(Double.MAX_VALUE)
                .maxWidth(Double.MAX_VALUE)
                .activeColor(Color.GREEN)
                .backgroundColor(Color.WHEAT)
                .skinType(Tile.SkinType.CUSTOM)
                .textVisible(false)
                .roundedCorners(true)
                .graphic(fragment.root)
                .build()

        tile.properties[componentIdName] = component.classType.simpleName!!
        if (isEditable) {
            val dndSkin = DndSkin(tile, tileSize)
            dndSkin.relocateRegion.visibleProperty().bind(tile.hoverProperty())
            dndSkin.closeRegion.visibleProperty().bind(tile.hoverProperty())
            dndSkin.resizeRegion.visibleProperty().bind(tile.hoverProperty())
            dndSkin.resizeRegion.backgroundColor = Tile.GRAY
            dndSkin.resizeRegion.foregroundColor = Tile.FOREGROUND
            dndSkin.closeRegion.backgroundColor = Tile.RED
            dndSkin.closeRegion.foregroundColor = Tile.FOREGROUND
            tile.skin = dndSkin
        }

        return tile
    }


    fun createTile(componentId : String,
                   isEditable: Boolean = false,
                   activeInstance: Boolean = false,
                   width : Double = tileSize,
                   height : Double = tileSize) : Tile {
        return createTile(components.getOrElse(componentId) {dummyComponent}, isEditable, activeInstance, width, height)
    }

    fun createTile(tile : Tile, isEditable: Boolean = false, activeInstance: Boolean = false) : Tile {
        return createTile(tile.properties[componentIdName] as String, isEditable, activeInstance, tile.width, tile.height)
    }

    fun refreshCounters() {
        components.forEach { (_, component) ->
            component.numInstances = component.maxInstances
        }
    }

    fun getId(tile : Tile) = tile.properties[componentIdName] as String

    private fun getComponent(tile: Tile) : VVComponent {
        val componentId = tile.properties[componentIdName] as String
        return components.getOrElse(componentId) {dummyComponent}
    }

}

data class VVComponent (val classType : KClass<out Fragment>,
                        val scope : KClass<out Scope>? = null,
                        val maxInstances : Int,
                        val required : Boolean = false) {

    val numInstancesProperty = SimpleIntegerProperty(maxInstances)
    var numInstances by numInstancesProperty

    fun getInstance(activeInstance: Boolean = false, editorScope: Scope? = null) : Fragment{
        if (activeInstance)
            this.numInstances--

        return if (scope != null)
            find(classType, scope.createInstance())
        else if (editorScope != null)
            find(classType, editorScope)
        else
            find(classType)
    }
}