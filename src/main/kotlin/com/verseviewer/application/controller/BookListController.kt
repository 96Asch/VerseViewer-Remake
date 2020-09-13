package com.verseviewer.application.controller

import com.verseviewer.application.model.Book
import com.verseviewer.application.model.Translation
import com.verseviewer.application.model.TranslationModel
import tornadofx.*

class BookListController : Controller() {

    private val dbController : DBController by inject()
    private val translationModel : TranslationModel by inject()

    val bookList = mutableListOf<Book>().asObservable()
    val translationList by lazy { initTranslationList() }

    private fun initTranslationList() = dbController.getTranslations().asObservable()

    fun populateBooks(translation : Translation) {
        bookList.clear()
        bookList.addAll(dbController.getBooksByTranslation(translation.name).filter {
            if (!translation.isDeutercanonic)
                it.type != "DC"
            else
               true
        })
    }



}