package com.example.demo.model

import javafx.beans.property.SimpleIntegerProperty
import tornadofx.getValue
import tornadofx.setValue

class TileProperties(x : Int, y : Int, colspan : Int, rowspan : Int) {
    val xProperty = SimpleIntegerProperty(x)
    var x by xProperty

    val yProperty = SimpleIntegerProperty(y)
    var y by yProperty

    val rowspanProperty = SimpleIntegerProperty(rowspan)
    var rowspan by rowspanProperty

    val colspanProperty = SimpleIntegerProperty(colspan)
    var colspan by colspanProperty
}
