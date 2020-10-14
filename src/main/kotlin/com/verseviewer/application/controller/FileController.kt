package com.verseviewer.application.controller

import javafx.stage.FileChooser
import tornadofx.*
import java.io.ByteArrayInputStream
import java.io.File
import javax.json.JsonArray
import javax.json.JsonObject

class FileController : Controller() {
    val scheduleExt = listOf(FileChooser.ExtensionFilter("Schedule File", ".vsched")).toTypedArray()

    fun writeJson(path : String, json : JsonObject) {
        File(path).writeText(json.toString())
    }

    fun writeJsonArray(path : String, jsonArray : JsonArray) {
        File(path).writeText(jsonArray.toPrettyString())
    }

    fun readJson(path : String) : JsonObject? {
        return ByteArrayInputStream(File(path).readBytes()).toJSON()
    }

    fun readJsonArray(path : String) : JsonArray? {
        return File(path).inputStream().toJSONArray()
    }

}