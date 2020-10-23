package com.verseviewer.application.model.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Users : IntIdTable("users"){
    val name = text("NAME")
    val layout = text("LAYOUT")

    override val primaryKey = PrimaryKey(id)
}


