package com.verseviewer.application.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue
import javax.json.JsonObject

class GridBuilder() : JsonModel {

    var tiles = FXCollections.observableArrayList<TileBuilder>()

    override fun toString(): String {
        return "GridBuilder(tiles=$tiles)"
    }

    override fun updateModel(json: JsonObject) {
        //TODO Fix empty json
        if (json.isNotEmpty()) {
            with(json) {
                tiles = getJsonArray("tiles").toModel()
            }
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("tiles", tiles.toJSON())
        }
    }
}

class TileBuilder(componentId : String = "", x : Int = 0, y : Int = 0, colspan : Int = 0, rowspan : Int = 0) : JsonModel {
    val componentIdProperty = SimpleStringProperty(componentId)
    var componentId by componentIdProperty

    val xProperty = SimpleIntegerProperty(x)
    var x by xProperty

    val yProperty = SimpleIntegerProperty(y)
    var y by yProperty

    val rowspanProperty = SimpleIntegerProperty(rowspan)
    var rowspan by rowspanProperty

    val colspanProperty = SimpleIntegerProperty(colspan)
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