package com.example.demo.app

import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val transparent by cssclass()
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        transparent {
            backgroundColor += c(0,100,0, 0.toDouble())
            baseColor = c(0,100,0, 0.5)
            borderRadius += box(25.px)
        }
    }
}