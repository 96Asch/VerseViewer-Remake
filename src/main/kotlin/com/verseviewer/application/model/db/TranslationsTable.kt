package com.verseviewer.application.model.db

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object Translations : IdTable<String>() {
    var name = text("NAME").uniqueIndex()
    var abbreviation = text("ABBREVIATION")
    var lang = text("LANGUAGE")
    var isDeutercanonic = bool("DEUTERCANONIC")

    override val id = name.entityId()
    override val primaryKey = PrimaryKey(name)
}

class TranslationDAO(id : EntityID<String>) : Entity<String>(id) {
    companion object: EntityClass<String, TranslationDAO>(Translations)

    var abbreviation by Translations.abbreviation
    var lang by Translations.lang
    var isDeutercanonic by Translations.isDeutercanonic
}
