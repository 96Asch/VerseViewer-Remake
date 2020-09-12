package com.verseviewer.application.app

import com.verseviewer.application.view.booklist.BookList
import com.verseviewer.application.view.dashboard.DashBoardEditor
import tornadofx.*

class MyApp: App(BookList::class, Styles::class)