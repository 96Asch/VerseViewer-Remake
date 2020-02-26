package com.example.demo.model

import com.example.demo.controller.TileBuilderController
import com.example.demo.view.dashboard.DndSkin
import com.example.demo.view.schedule.Schedule
import com.example.demo.view.versebox.VerseBox
import eu.hansolo.tilesfx.Tile
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class DashBoardInfo(tiles : List<TileProperties>) {
    var tiles by property(tiles)
}

data class VVComponent (val classType : KClass<out Fragment>, val scope : KClass<out Scope>? = null) {
    fun getInstance() : Fragment{
        return if (scope != null)
            find(classType, scope.createInstance())
        else
            find(classType)
    }
}

class DashBoardModel : ItemViewModel<DashBoardInfo>() {

    private val components = listOf(VVComponent(VerseBox::class), VVComponent(Schedule::class, ScheduleScope::class))
    private var editMode = false
    private val minRowspan = 4
    private val minColspan = 4


    fun build(list: List<GridBuilder>, activeDb: Int, isEditable: Boolean = false) {
        editMode = isEditable
        list.asSequence().forEach {
            if (it.id == activeDb) {
                println(it)
                val tileProperties = it.tiles.map(::buildTileProperties)
                item = DashBoardInfo(tileProperties)
            }
        }
    }

    private fun buildTileProperties (tileBuilder : TileBuilder) : TileProperties {
        return TileProperties(createTile(components[tileBuilder.componentId-1]),
                                tileBuilder.x, tileBuilder.y, tileBuilder.colspan, tileBuilder.rowspan,
                                minColspan, minRowspan)
    }

    private fun createTile(component: VVComponent) : Tile {
        val fragment = component.getInstance()
        val testTile = eu.hansolo.tilesfx.TileBuilder.create()
                .activeColor(Color.GREEN)
                .backgroundColor(Color.WHEAT)
                .skinType(Tile.SkinType.CUSTOM)
                .roundedCorners(true)
                .graphic(fragment.root)
                .build()

        if (editMode) {
            val dndSkin = DndSkin(testTile)
            dndSkin.resizeRegion.backgroundColor = Tile.GRAY
            dndSkin.resizeRegion.foregroundColor = Tile.DARK_BLUE
            dndSkin.closeRegion.backgroundColor = Tile.RED
            testTile.skin = dndSkin
        }
        testTile.isTextVisible = false
        return testTile
    }
}
