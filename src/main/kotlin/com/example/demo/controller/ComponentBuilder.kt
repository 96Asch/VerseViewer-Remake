package com.example.demo.controller

import com.example.demo.model.scope.ScheduleScope
import com.example.demo.view.Dummy
import com.example.demo.view.dashboard.DndSkin
import com.example.demo.view.schedule.Schedule
import com.example.demo.view.versebox.VerseBox
import eu.hansolo.tilesfx.Tile
import eu.hansolo.tilesfx.TileBuilder
import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class ComponentBuilder : Controller() {

    private val componentIdName = "componentId"
    val tileSize = 25.0
    private val dummyComponent = VVComponent(Dummy::class)
    val components = mapOf(
            VerseBox::class.simpleName to VVComponent(VerseBox::class, required = true),
            Schedule::class.simpleName to VVComponent(Schedule::class, ScheduleScope::class, true)
    )

    fun createTile(component : VVComponent, id : String, isEditable: Boolean = false) : Tile {

        val testTile = TileBuilder.create()
                .prefWidth(tileSize)
                .prefHeight(tileSize)
                .prefSize(tileSize, tileSize)
                .activeColor(Color.GREEN)
                .backgroundColor(Color.WHEAT)
                .skinType(Tile.SkinType.CUSTOM)
                .textVisible(false)
                .roundedCorners(true)
                .infoRegionBackgroundColor(Tile.LIGHT_RED)
                .infoRegionForegroundColor(Tile.FOREGROUND)
                .graphic(component.getInstance().root)
                .build()

        testTile.properties[componentIdName] = id
        if (isEditable) {
            val dndSkin = DndSkin(testTile, tileSize)
            dndSkin.relocateRegion.visibleProperty().bind(testTile.hoverProperty())
            dndSkin.closeRegion.visibleProperty().bind(testTile.hoverProperty())
            dndSkin.resizeRegion.visibleProperty().bind(testTile.hoverProperty())
            dndSkin.resizeRegion.backgroundColor = Tile.GRAY
            dndSkin.resizeRegion.foregroundColor = Tile.FOREGROUND
            dndSkin.closeRegion.backgroundColor = Tile.RED
            dndSkin.closeRegion.foregroundColor = Tile.FOREGROUND
            testTile.skin = dndSkin
        }
//        testTile.infoRegionTooltipText = "Required component"
//        testTile.showInfoRegion(component.required && component.instancesInUse == 0)

        return testTile
    }


    fun createTile(componentId : String, isEditable: Boolean = false) : Tile {
        val component = if (components.containsKey(componentId)) components[componentId] else dummyComponent
        return createTile(component!!, componentId, isEditable)
    }

    fun createTile(tile : Tile, isEditable: Boolean = false) : Tile {
        return createTile(tile.properties[componentIdName] as String, isEditable)
    }

}

data class VVComponent (val classType : KClass<out Fragment>,
                        val scope : KClass<out Scope>? = null,
                        val multipleEnabled : Boolean = false,
                        val required : Boolean = false) {

    fun getInstance() : Fragment{
        return if (scope != null)
            find(classType, scope.createInstance())
        else
            find(classType)
    }
}