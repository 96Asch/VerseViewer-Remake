package com.verseviewer.application.controller

import com.verseviewer.application.model.Passage
import javafx.scene.Node
import javafx.scene.text.Text
import tornadofx.Controller

class PassageBoxController : Controller() {

    val uiElements = mutableListOf<Node>()
    val passageTexts = mutableListOf<Pair<Text, Text>>()

    val wrapWidth = 500.0


    fun buildText(list : List<Passage>) {
        var lastY = 10.0
        val bookSorted = sortByBooks(list)

        passageTexts.clear()

        bookSorted.forEach {
            val header = Text(formatHeader(it.key, it.value)).apply {
                y = lastY
                wrappingWidth = wrapWidth
            }
            val body = Text(it.value.joinToString("\n") { ps -> ps.text }).apply {
                yProperty().bind(header.yProperty().add(header.boundsInLocal.height))
                lastY = this.boundsInLocal.height + this.y
                wrappingWidth = wrapWidth
            }

            passageTexts.add(Pair(header, body))
        }
    }


    private fun formatHeader(book : String, passages : List<Passage>): String {
        var headerString = "$book "
        var mode = FormatMode.NONE

        for (i in passages.indices) {
            val ps = passages[i]
            mode = getMode(mode, i, passages)

            when (mode) {
                FormatMode.NEW_CHAPTER -> {
                    if (i != 0)
                        headerString += ", "
                    headerString += "${ps.chapter}:${ps.verse}"
                }
                FormatMode.NORMAL, FormatMode.BEGIN_ADJACENT -> {
                    headerString += ",${ps.verse}"
                }
                FormatMode.END_ADJACENT -> {
                    headerString += "-${ps.verse.last()}"
                }
                else -> {}
            }
            println("$mode - $i")
        }
        println(headerString)
        return headerString
    }

    private fun getMode(mode : FormatMode, pass: Int, passages: List<Passage>): FormatMode {
        return when {
            (pass == 0 || (pass-1 >= 0 && passages[pass-1].chapter != passages[pass].chapter)) -> FormatMode.NEW_CHAPTER

            ((pass+1 < passages.size && passages[pass].isAdjacentTo(passages[pass+1]))
                    && ((mode == FormatMode.IN_ADJACENT || mode == FormatMode.BEGIN_ADJACENT || mode == FormatMode.NEW_CHAPTER)
                        || pass+2 < passages.size && passages[pass+1].isAdjacentTo(passages[pass+2]))) -> {
                if (mode == FormatMode.IN_ADJACENT || mode == FormatMode.BEGIN_ADJACENT || (mode == FormatMode.NEW_CHAPTER && (pass-1 >= 0 && passages[pass-1].isAdjacentTo(passages[pass]))))
                    FormatMode.IN_ADJACENT
                else
                    FormatMode.BEGIN_ADJACENT
            }

            (mode == FormatMode.IN_ADJACENT
                    && (pass+1 == passages.size || (pass+1 < passages.size && !passages[pass].isAdjacentTo(passages[pass+1])))) -> FormatMode.END_ADJACENT
            else -> FormatMode.NORMAL
        }
    }



    private fun sortByBooks(list : List<Passage>): MutableMap<String, MutableList<Passage>> {
        val sorted = mutableMapOf<String, MutableList<Passage>>()
        list.forEach {
            if (sorted.containsKey(it.book))
                sorted[it.book]!!.add(it)
            else
                sorted[it.book] = mutableListOf(it)
        }
        return sorted
    }

    private enum class FormatMode {
        NEW_CHAPTER,
        BEGIN_ADJACENT,
        IN_ADJACENT,
        END_ADJACENT,
        NORMAL,
        NONE
    }


}


