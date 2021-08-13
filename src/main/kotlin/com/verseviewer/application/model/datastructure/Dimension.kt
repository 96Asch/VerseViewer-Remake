package com.verseviewer.application.model.datastructure

data class Dimension(val x : Int, val y: Int, val width: Int, val height: Int) {
    infix fun intersects(other: Dimension) : Boolean {
        val x2 = x + width
        val y2 = y + height
        val otherX2 = other.x + other.width
        val otherY2 = other.y + other.height
        return (x < otherX2 && x2 > other.x && y < otherY2 && y2 > other.y)
    }
}