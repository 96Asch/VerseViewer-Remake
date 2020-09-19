package com.verseviewer.application.model.datastructure

class Range (val range: ClosedRange<Int>) {

    override fun toString(): String {
        return when (range.start) {
            range.endInclusive -> range.start.toString()
            else -> "${range.start}-${range.endInclusive}"
        }
    }

    infix fun inRangeFirst(verse: Range): Boolean {
        return range.contains(verse.range.start)
    }

    fun first() = range.start

    fun last() = range.endInclusive
}

infix fun Int.inRange(range: Range) : Boolean {
    return this in range.range
}
