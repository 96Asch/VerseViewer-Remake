package com.verseviewer.application.view.components.dummy

import tornadofx.*

class Dummy : Fragment("My View") {
    override val root = borderpane {
        center = label("DUMMY") {  }
    }
}
