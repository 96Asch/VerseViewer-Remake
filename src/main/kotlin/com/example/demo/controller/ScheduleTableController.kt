package com.example.demo.controller

import com.example.demo.model.Verse
import com.example.demo.model.datastructure.GroupType
import com.example.demo.model.datastructure.VerseGroup
import javafx.scene.control.TableView
import tornadofx.*

class ScheduleTableController : Controller() {

    val list = mutableListOf<VerseGroup>().observable()
    val detailList = listOf<Verse>().observable()

    fun setAll(l: List<VerseGroup>) {
        list.setAll(l)
    }

    fun setDetail(vg: VerseGroup) {
        detailList.setAll(vg.verses)
    }

    fun moveSelectedUp(tv: TableView<VerseGroup>) {
        val selectModel = tv.selectionModel
        val indices = selectModel.selectedIndices.toMutableList()

        selectModel.clearSelection()
        indices.forEachIndexed { i, selectedIndex ->
            if (selectedIndex > 0) {
                if (selectedIndex - 1 !in indices) {
                    list.swap(selectedIndex, selectedIndex - 1)
                    indices[i]--
                }
            }
        }
        indices.forEach { selectModel.select(it) }
    }

    fun moveSelectedDown(tv: TableView<VerseGroup>) {
        val selectModel = tv.selectionModel
        val lastIndex = tv.items.size - 1
        val indices = selectModel.selectedIndices.reversed().toMutableList()

        selectModel.clearSelection()
        indices.forEachIndexed { i, selectedIndex ->
            println("$indices, $selectedIndex")
            if (selectedIndex < lastIndex) {
                if (selectedIndex + 1 !in indices) {
                    list.swap(selectedIndex, selectedIndex + 1)
                    indices[i]++
                }
            }
        }
        indices.forEach { selectModel.select(it) }
    }

    fun groupSelected(tv: TableView<VerseGroup>) {
        if (tv.items.isEmpty()) return

        val selectModel = tv.selectionModel
        val firstIndex = selectModel.selectedIndices.first()
        selectModel.selectedIndices.forEach {
            if (it != firstIndex) {
                list[firstIndex].merge(list[it])
            }
        }
        selectModel.selectedIndices.reversed().forEach {
            if (it != firstIndex)
                list.removeAt(it)
        }
        selectModel.clearAndSelect(firstIndex)
        detailList.setAll(tv.selectedItem?.verses)
    }

    //todo: Allow multi selection
    fun ungroupSelected(tv: TableView<VerseGroup>) {
        if (tv.items.isEmpty()) return
        val selectModel = tv.selectionModel
        if (selectModel.selectedIndices.isEmpty()) return

        val firstIndex = selectModel.selectedIndices.first()
        if (list[firstIndex].verses.size > 1) {
            val newList = list[firstIndex].verses.map { v ->
                VerseGroup(mutableListOf(v), GroupType.MONO_TRANSLATION)
            }
            list.removeAt(firstIndex)
            list.addAll(firstIndex, newList)
        }

        selectModel.clearAndSelect(firstIndex)
        detailList.setAll(tv.selectedItem?.verses)
    }

    fun deleteSelected(tv: TableView<VerseGroup>) {
        if (tv.items.isEmpty()) return
        val selectModel = tv.selectionModel
        if (selectModel.selectedIndices.isEmpty()) return

        val toRemove = selectModel.selectedItems
        list.removeAll(toRemove)
    }

    fun clear() {
        list.clear()
    }

}