package com.example.demo.model

import eu.hansolo.tilesfx.Tile
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.getValue
import tornadofx.setValue

class TileProperties(tile: Tile, x : Int, y : Int, colspan : Int, rowspan : Int, minColspan : Int, minRowSpan : Int) {

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

    val minRowSpanProperty = SimpleIntegerProperty(minRowSpan)
    var minRowSpan by minRowSpanProperty

    val minColspanProperty = SimpleIntegerProperty(minColspan)
    var minColspan by minColspanProperty
}
