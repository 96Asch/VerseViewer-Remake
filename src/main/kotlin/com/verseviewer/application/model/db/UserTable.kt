package com.verseviewer.application.model.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Users : IntIdTable("users"){
    val name: Column<String> = text("NAME")
    val layout: Column<String> = text("LAYOUT")
}


class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(Users)
    var name by Users.name
    var layout by Users.layout
}



