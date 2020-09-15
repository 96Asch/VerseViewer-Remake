package com.verseviewer.application.controller

import com.verseviewer.application.model.Book
import com.verseviewer.application.model.Translation
import com.verseviewer.application.model.TranslationModel
import com.verseviewer.application.model.event.BroadcastVerses
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*

class BookListController : Controller() {

    private val dbController : DBController by inject()
    private val translationModel : TranslationModel by inject()

    val bookList = mutableListOf<Book>().asObservable()
    val translationList by lazy { dbController.getTranslations().asObservable() }

    fun populateBooks(translation : Translation) {
        bookList.clear()
        bookList.addAll(dbController.getBooksByTranslation(translation.name).filter {
            if (!translation.isDeutercanonic)
                it.type != "DC"
            else
               true
        })
    }

    fun sendVerses(translation : String, bookId : Int) {
        val verses = dbController.getBookVerses(translation, bookId)
        fire(BroadcastVerses(verses))
    }



}