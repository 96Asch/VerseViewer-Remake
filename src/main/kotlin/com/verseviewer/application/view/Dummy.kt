package com.verseviewer.application.view

import tornadofx.*

class Dummy : Fragment("My View") {
    override val root = borderpane {
        center = label("DUMMY") {  }
    }
}
