package com.verseviewer.application.model.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Preferences : IntIdTable("preferences") {
    val owner = integer("OWNER")

    val name = text("NAME")

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
}

class PreferenceDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PreferenceDAO>(Preferences)

    var owner by Preferences.owner

    var name by Preferences.name

    var display by Preferences.display

    var textAlignment by Preferences.textAlignment
    var fontFamily by Preferences.fontFamily
    var fontSize by Preferences.fontSize
    var fontWeight by Preferences.fontWeight
    var fontPosture by Preferences.fontPosture

    var textFill by Preferences.textFill
    var textStroke by Preferences.textStroke
    var textStrokeWidth by Preferences.textStrokeWidth

    var orientation by Preferences.orientation
}