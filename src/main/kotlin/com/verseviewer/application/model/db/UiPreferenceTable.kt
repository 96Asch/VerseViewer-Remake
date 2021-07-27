package com.verseviewer.application.model.db

import org.jetbrains.exposed.dao.id.IntIdTable

object UiPreferences : IntIdTable("uipreferences") {

    val userId = integer("USER")
    val layout = text("LAYOUT")
    val tileColor = text("TILE_COLOR")

    val roundedCorner = bool("ROUNDED_CORNERS")

    val fontFamily = text("FONT_FAMILY")
    val fontSize = double("FONT_SIZE")
    val fontWeight = text("FONT_WEIGHT")
    val fontPosture = text("FONT_POSTURE")

    override val primaryKey = PrimaryKey(id)
}