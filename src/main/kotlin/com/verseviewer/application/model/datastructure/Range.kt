package com.verseviewer.application.model.datastructure

import tornadofx.*
import javax.json.JsonObject

class Range (var first : Int, var last : Int) : JsonModel {

    override fun toString(): String {
        return when (first) {
            last -> "$first"
            else -> "$first-$last"
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            first = int("first") ?: 0
            last = int("last") ?: 0
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("first", first)
            add("last", last)
        }
    }
}

infix fun Int.inRange(range: Range) : Boolean {
    return range.first <= this && this <= range.last
}
