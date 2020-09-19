package com.verseviewer.application.controller

import com.verseviewer.application.model.Passage
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.Node
import javafx.scene.text.Font
import javafx.scene.text.Text
import tornadofx.Controller

class PassageBoxController : Controller() {

    val uiElements = mutableListOf<Node>()
    val passageTexts = mutableListOf<Pair<Text, Text>>()

    val wrapWidth = 500.0
    val startY = 10.0

    val containerWidthProperty = SimpleDoubleProperty()
    val fs = 35.0



    fun buildText(list : List<Passage>) {
        val bookSorted = sortByBooks(list)
        var lastText : Text? = null

        passageTexts.clear()

        bookSorted.forEach {
            val header = Text(formatHeader(it.key, it.value) + "\n").apply {
                if (lastText == null)
                    y = startY
                else
                    yProperty().bind(lastText!!.yProperty().add(lastText!!.boundsInLocal.height))
                wrappingWidth = containerWidthProperty.value - 20.0
                font = Font.font(fs)
            }
            val body = Text(it.value.joinToString("\n") { ps -> ps.text } + "\n").apply {
                yProperty().bind(header.yProperty().add(header.boundsInLocal.height))
                wrappingWidth = containerWidthProperty.value - 20.0
                font = Font.font(fs)

            }
            lastText = body

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


