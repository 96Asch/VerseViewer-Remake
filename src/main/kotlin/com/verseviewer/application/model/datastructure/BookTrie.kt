package com.verseviewer.application.model.datastructure

import com.verseviewer.application.model.Book

class BookTrie() {

    data class Node (var isEnd: Boolean = false, var id: Int = 0, val children : MutableMap<Char, Node> = mutableMapOf())

    private val root = Node()

    fun clear() {
        root.children.clear()
    }

    fun insert (book: Book) {
        insert(book.name.uppercase().replace("\\s".toRegex(), ""), book.book_id)
    }

    private fun insert (word: String, id: Int) {
        var currentNode = root

        word.uppercase().forEach {
            currentNode.children[it] = currentNode.children[it] ?: Node()
            currentNode = currentNode.children[it]!!
        }
        currentNode.isEnd = true
        currentNode.id = id
    }

    fun retrieve (word: String) : MutableMap<String, Int> {
        var currentNode = root
        var str = String()

        for (char in word.uppercase().replace("\\s".toRegex(), "")) {
            if (currentNode.children[char] == null) {
                break
            }
            str += char
            currentNode = currentNode.children[char]!!
        }
        val map = mutableMapOf<String, Int>()
        val unique = mutableListOf<Int>()

        if (currentNode.isEnd) {
            map[str] = currentNode.id
            unique += currentNode.id
        }
        else if (!str.isEmpty())
            autocomplete(str, currentNode, map, unique)

        return map
    }

    private fun autocomplete(prefix : String, node : Node?, map : MutableMap<String, Int>, unique : MutableList<Int>) {
        node?.children?.forEach {
            if (it.value.isEnd && !unique.contains(it.value.id)) {
                map[prefix + it.key] = it.value.id
                unique += it.value.id
            }
            autocomplete(prefix + it.key, it.value, map, unique)
        }
    }

}
