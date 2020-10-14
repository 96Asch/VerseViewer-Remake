package com.verseviewer.application.controller

import com.verseviewer.application.model.Passage
import tornadofx.Controller

class PassageBoxController : Controller() {

    val passageStrings = mutableListOf<Pair<String, String>>()

    fun buildTexts(list : List<Passage>): List<Pair<String, String>> {
        val strings =  sortByBooks(list).map {
            val (header, body) = formatProjection(it.key, it.value)
            Pair(header, body)
        }
        passageStrings.clear()
        passageStrings.addAll(strings)
        return strings
    }

    private fun formatProjection(book : String, passages : List<Passage>): Pair<String, String> {
        var headerString = "$book "
        var bodyString = ""
        var mode = FormatMode.NONE

        for (i in passages.indices) {
            val ps = passages[i]
            mode = getMode(mode, i, passages)

            when (mode) {
                FormatMode.NEW_CHAPTER -> {
                    if (i == 0) {
                        bodyString += "${ps.text} "
                    }
                    else {
                        bodyString += "\n${ps.text} "
                        headerString += ", "
                    }
                    headerString += "${ps.chapter}:${ps.verse}"

                }
                FormatMode.NORMAL, FormatMode.BEGIN_ADJACENT -> {
                    headerString += ",${ps.verse}"
                    bodyString += ps.text + ' '
                }
                FormatMode.END_ADJACENT -> {
                    headerString += "-${ps.verse.last}"
                    bodyString += ps.text + ' '
                }
                else -> { bodyString += ps.text + ' '}
            }
        }
        return Pair(headerString,bodyString)
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


