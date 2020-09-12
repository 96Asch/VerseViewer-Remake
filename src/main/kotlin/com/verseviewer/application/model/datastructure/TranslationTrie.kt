package com.verseviewer.application.model.datastructure

import com.verseviewer.application.model.Translation

class TranslationTrie {
    class Node (var isEnd: Boolean = false, var translation: Translation? = null, val children : MutableMap<Char, Node> = mutableMapOf())

    private val root = Node()

    fun insert(translation: Translation) {
        insert(translation.abbreviation, translation)
    }

    private fun insert (word: String, translation: Translation) {
        var currentNode = root

        word.toUpperCase().forEach {
            currentNode.children[it] = currentNode.children[it] ?: Node()
            currentNode = currentNode.children[it]!!
        }
        currentNode.isEnd = true
        currentNode.translation = translation
    }

    fun retrieve (word: String) : MutableList<Translation> {
        var currentNode = root
        var str = String()

        for (char in word.toUpperCase()) {
            if (currentNode.children[char] == null) {
                break
            }
            str += char
            currentNode = currentNode.children[char]!!
        }
        val list = mutableListOf<Translation>()

        if (currentNode.isEnd)
            list += currentNode.translation!!
        if (str.isNotEmpty())
            autocomplete(str, currentNode, list)

        return list
    }

    private fun autocomplete(prefix : String, node : Node?, list : MutableList<Translation>) {
        node?.children?.forEach {
            if (it.value.isEnd)
                list += it.value.translation!!
            autocomplete(prefix + it.key, it.value, list)
        }
    }
}