package com.example.demo.model.datastructure

import com.example.demo.model.Translation

class TranslationTrie {
    class Node (var isEnd: Boolean = false, var name: String = "", val children : MutableMap<Char, Node> = mutableMapOf())

    private val root = Node()

    fun insert(translation: Translation) {
        insert(translation.abbreviation, translation.name)
        insert(translation.name, translation.name)
    }

    private fun insert (word: String, name: String) {
        var currentNode = root

        word.toUpperCase().forEach {
            currentNode.children[it] = currentNode.children[it] ?: Node()
            currentNode = currentNode.children[it]!!
        }
        currentNode.isEnd = true
        currentNode.name = name
    }

    fun retrieve (word: String) : MutableList<String> {
        var currentNode = root
        var str = String()

        for (char in word.toUpperCase()) {
            if (currentNode.children[char] == null) {
                break
            }
            str += char
            currentNode = currentNode.children[char]!!
        }
        val list = mutableListOf<String>()

        if (currentNode.isEnd)
            list += currentNode.name
        if (str.isNotEmpty())
            autocomplete(str, currentNode, list)

        return list
    }

    private fun autocomplete(prefix : String, node : Node?, list : MutableList<String>) {
        node?.children?.forEach {
            if (it.value.isEnd)
                list += it.value.name
            autocomplete(prefix + it.key, it.value, list)
        }
    }
}