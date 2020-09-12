package com.verseviewer.application.model.db

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

abstract class BooksTable(langCode: String) : Table("BOOKS") {
    val bookId = integer("BOOK_ID")
    val name = text("NAME_$langCode")
    val type = text("TYPE")

    override val tableName = "BOOKS"
    override val primaryKey = PrimaryKey(bookId)
}
