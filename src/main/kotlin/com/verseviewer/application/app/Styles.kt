package com.verseviewer.application.app

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.*
import kotlin.random.Random

class Styles : Stylesheet() {

    companion object {
        val highlightTile by cssclass()
        val transparent by cssclass()
        val placementAllowed by cssclass()
        val placementNotAllowed by cssclass()
        val partialTransparant by cssclass()
        val greyedOut by cssclass()
        val invisible by cssclass()

        val translationHeader by cssclass()
        val passageHeader by cssclass()
        val passageBody by cssclass()
        val frame by cssclass()

        var fontsize = 10.0
    }

    init {

        transparent {
//            backgroundColor += c(0,100,0, 0.toDouble())
            backgroundColor += c(0, 100, 100, 0.05)
//            borderRadius += box(25.px)
        }

        invisible {
            backgroundColor += c(0, 0, 0, 0.5)
        }

        translationHeader {
            stroke = c(0.0,0.0,0.0)
            fill = c(237, 236, 173)
            strokeWidth = 1.px
        }

        passageHeader {
            stroke = c(0.0,0.0,0.0)
            strokeWidth = 3.px
            fill = c(255, 255, 255)
        }

        passageBody {
            stroke = c(0.0,0.0,0.0)
            strokeWidth = 2.px
            fill = c(255, 255, 255)
        }

        frame {
            stroke = c(33, 33, 30)
            strokeWidth = 7.0.px
        }

        highlightTile {
            and (hover) {
                opacity = 0.4
            }
        }

        partialTransparant {
            opacity = 0.8
        }


        placementAllowed {
            backgroundColor += c(0,100,0,0.5)
        }

        placementNotAllowed {
            backgroundColor += c(100, 0, 0, 0.8)
        }

        greyedOut {
            opacity = 0.1
        }
    }
}