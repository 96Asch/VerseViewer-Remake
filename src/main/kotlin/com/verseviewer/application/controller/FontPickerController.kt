package com.verseviewer.application.controller

import javafx.collections.FXCollections
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.Controller

class FontPickerController : Controller() {
    val fontfamilyList = FXCollections.observableArrayList(Font.getFamilies())
    val fontWeightList = FXCollections.observableArrayList(FontWeight.values().asList())
    val fontPostureList = FXCollections.observableArrayList(FontPosture.values().asList())

}