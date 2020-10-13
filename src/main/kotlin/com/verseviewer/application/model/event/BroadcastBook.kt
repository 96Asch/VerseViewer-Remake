package com.verseviewer.application.model.event

import com.verseviewer.application.model.Book
import tornadofx.FXEvent

class BroadcastBook(val book : Book) : FXEvent()