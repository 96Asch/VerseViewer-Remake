package com.example.demo.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue
import javax.json.JsonObject

class GridBuilder() : JsonModel {
    val idProperty = SimpleIntegerProperty()
    var id by idProperty

    var tiles = listOf<TileBuilder>()

    override fun toString(): String {
        return "GridBuilder(id=$id, tiles=$tiles)"
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            id = int("id") ?: 0
            tiles = getJsonArray("tiles").toModel()
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id",  id)
            add("tiles", tiles.toJSON())
        }
    }
}

class TileBuilder() : JsonModel {
    val componentIdProperty = SimpleStringProperty()
    var componentId by componentIdProperty

    val xProperty = SimpleIntegerProperty()
    var x by xProperty

    val yProperty = SimpleIntegerProperty()
    var y by yProperty

    val rowspanProperty = SimpleIntegerProperty()
    var rowspan by rowspanProperty

    val colspanProperty = SimpleIntegerProperty()
    var colspan by colspanProperty

    override fun toString(): String {
        return "TileBuilder(componentId=$componentId, x=$x, y=$y, rowspan=$rowspan, colspan=$colspan)"
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            componentId = string("componentId") ?: ""
            x = int("x") ?: -1
            y = int("y") ?: -1
            rowspan = int("rowspan") ?: -1
            colspan = int("colspan") ?: -1
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("componentId", componentId)
            add("x", x)
            add("y", y)
            add("rowspan", rowspan)
            add("colspan", colspan)
        }
    }


}