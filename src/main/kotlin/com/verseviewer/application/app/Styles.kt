package com.verseviewer.application.app

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val highlightTile by cssclass()
        val transparent by cssclass()
        val placementAllowed by cssclass()
        val placementNotAllowed by cssclass()
        val partialTransparant by cssclass()
        val greyedOut by cssclass()

        val anchorPaneTest by cssclass()
        val anchorPaneTest2 by cssclass()
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        transparent {
//            backgroundColor += c(0,100,0, 0.toDouble())
            backgroundColor += c(0, 100, 100, 0.05)
//            borderRadius += box(25.px)
        }

        anchorPaneTest {
            backgroundColor += c(100,100,20,1.0)
        }

        anchorPaneTest2 {
            backgroundColor += c(100,0,20,1.0)
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