package com.example.demo.model.datastructure

import com.example.demo.model.Verse
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.Node
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue
import java.io.Serializable

enum class GroupType {
    MONO_TRANSLATION,
    POLY_TRANSLATION
}

class VerseGroup (verses: List<Verse>, var type : GroupType) : Serializable {
    val versesProperty = SimpleListProperty<Verse>(FXCollections.observableArrayList(verses))
    var verses by versesProperty


    fun merge(group : VerseGroup) {
        verses.addAll(group.verses)

        if (group.type == GroupType.MONO_TRANSLATION) {
            if (group.verses.first()?.book != verses.first()?.book)
                type = GroupType.POLY_TRANSLATION
        }
        else
            type = GroupType.POLY_TRANSLATION
    }

    override fun toString(): String {
        val ids = verses.joinToString { it.id.toString() }
        return "VerseGroup {type: $type, ids: $ids}"
    }
}
