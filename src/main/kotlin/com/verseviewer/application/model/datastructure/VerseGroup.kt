package com.verseviewer.application.model.datastructure

import com.verseviewer.application.model.Passage
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleMapProperty
import javafx.collections.FXCollections
import tornadofx.getValue
import tornadofx.observable
import tornadofx.setValue
import tornadofx.sizeProperty
import java.io.Serializable

class VerseGroup (passages: List<Passage>) : Serializable {
    val versesProperty = SimpleListProperty<Passage>(FXCollections.observableArrayList(passages))
    var verses by versesProperty

    val translationSortedProperty by lazy {
        SimpleListProperty<List<Passage>>(FXCollections.observableArrayList(sortedByTranslation(passages).values))
    }
    var translationSorted by translationSortedProperty

    fun merge(group : VerseGroup) {
        verses.addAll(group.verses)
        translationSorted = FXCollections.observableArrayList(sortedByTranslation(verses).values)
    }

    private fun sortedByTranslation(list : List<Passage>): Map<String, MutableList<Passage>> {
        val sorted = mutableMapOf<String, MutableList<Passage>>()

        list.forEach {
            if (sorted.containsKey(it.translation.name)) {
                sorted[it.translation.name]!!.add(it)
            }
            else {
                sorted[it.translation.name] = mutableListOf(it)
            }
        }
        return sorted
    }

    override fun toString(): String {
        val ids = verses.joinToString { it.id.toString() }
        return "VerseGroup {ids: $ids}"
    }
}
