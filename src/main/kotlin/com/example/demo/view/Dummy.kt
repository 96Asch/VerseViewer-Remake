package com.example.demo.view

import tornadofx.*

class Dummy : Fragment("My View") {
    override val root = borderpane {
        center = label("DUMMY") {  }
    }
}
