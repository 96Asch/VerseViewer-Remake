package com.verseviewer.application.model.db

import org.jetbrains.exposed.sql.Table

abstract class TranslationBible(name: String) : Table("") {
    private val internalTableName = name


    val verseId = integer("ID")
    val bookId  = integer("BOOK_ID")
    val chapter = integer("CHAPTER")
    val verse = text("VERSE")
    val text = text("TEXT") 

    override val tableName: String = internalTableName
    override val primaryKey = PrimaryKey(verseId)
}