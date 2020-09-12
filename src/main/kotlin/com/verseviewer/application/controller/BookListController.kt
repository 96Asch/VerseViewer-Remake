package com.verseviewer.application.controller

import com.verseviewer.application.model.TranslationModel
import tornadofx.*

class BookListController : Controller() {

    private val dbController : DBController by inject()
    private val translationModel : TranslationModel by inject()

    var booklist = mutableListOf<String>()
}