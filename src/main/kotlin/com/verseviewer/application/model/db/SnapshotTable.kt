package com.verseviewer.application.model.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object SnapshotTable : IntIdTable("snapshots"){
    val name = text("NAME")
    val layout = text("LAYOUT")

    val display = integer("DISPLAY")
    val textAlignment = text("TEXTALIGNMENT")

    val fontFamily = text("FONT_FAMILY")
    val fontSize = double("FONT_SIZE")
    val fontWeight = text("FONT_WEIGHT")
    val fontPosture = text("FONT_POSTURE")

    val textFill = text("TEXT_FILL")
    val textStroke = text("TEXT_STROKE")
    val textStrokeWidth = double("TEXT_STROKE_WIDTH")

    val orientation = text("ORIENTATION")

    override val primaryKey = PrimaryKey(id)
}


