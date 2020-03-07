package com.example.demo.model

import com.example.demo.model.datastructure.Copyable
import eu.hansolo.tilesfx.Tile
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*

class TileProperties(tile : Tile, x : Int, y : Int, colspan : Int, rowspan : Int) : Copyable {
    val tileProperty = SimpleObjectProperty<Tile>(tile)
    var tile by tileProperty

    val xProperty = SimpleIntegerProperty(x)
    var x by xProperty

    val yProperty = SimpleIntegerProperty(y)
    var y by yProperty

    val rowspanProperty = SimpleIntegerProperty(rowspan)
    var rowspan by rowspanProperty

    val colspanProperty = SimpleIntegerProperty(colspan)
    var colspan by colspanProperty

    override fun copy() = TileProperties(tile, x, y, colspan, rowspan)


    override fun toString(): String {
        return "TileProperties(x=$x, y=$y, rowspan=$rowspan, colspan=$colspan)"
    }
}

class TilePropertiesModel : ItemViewModel<TileProperties>() {
    val tile = bind(TileProperties::tileProperty, autocommit = true)
    val x : SimpleIntegerProperty  = bind(TileProperties::xProperty, autocommit = true)
    val y : SimpleIntegerProperty  = bind(TileProperties::yProperty, autocommit = true)
    val rowspan : SimpleIntegerProperty = bind(TileProperties::rowspanProperty, autocommit = true)
    val colspan : SimpleIntegerProperty =  bind(TileProperties::colspanProperty, autocommit = true)

    fun setCoordinate(x: Int, y: Int) {
        this.x.value = x;
        this.y.value = y
    }

    fun setSpans(col: Int, row : Int) {
        rowspan.value = row
        colspan.value = col
    }
}

