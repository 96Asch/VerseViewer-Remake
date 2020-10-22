package com.verseviewer.application.model

import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import tornadofx.*
import javax.json.JsonObject

class VerseGroup (passages: List<Passage> = listOf()) : JsonModel {
    val verses = FXCollections.observableArrayList(passages)

    val translationSorted by lazy {
       FXCollections.observableArrayList(sortedByTranslation(passages).values)
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            verses.setAll(getJsonArray("verses").toModel())
            translationSorted.setAll(sortedByTranslation(verses).values)
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("verses", verses.toJSON())
        }
    }

    fun merge(group : VerseGroup) {
        verses.addAll(group.verses)
        translationSorted.setAll(sortedByTranslation(verses).values)
    }

    private fun sortedByTranslation(list : List<Passage>): Map<String, MutableList<Passage>> {
        val sorted = mutableMapOf<String, MutableList<Passage>>()

        list.forEach {
            if (sorted.containsKey(it.translation.name))
                sorted[it.translation.name]!!.add(it)
            else
                sorted[it.translation.name] = mutableListOf(it)
        }
        return sorted
    }


    override fun toString(): String {
        val ids = verses.joinToString { it.id.toString() }
        return "VerseGroup {ids: $ids}"
    }

    init {

    }
}

class VerseGroupModel : ItemViewModel<VerseGroup>() {
    var group = bind(VerseGroup::verses)
    var sorted = bind(VerseGroup::translationSorted)
}