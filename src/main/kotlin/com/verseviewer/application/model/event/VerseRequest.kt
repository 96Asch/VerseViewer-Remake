package com.verseviewer.application.model.event

import tornadofx.*

class VerseRequest (val translation : String, val books : Map<String, Int>, val chapter : Int, val verse : Int) : FXEvent()