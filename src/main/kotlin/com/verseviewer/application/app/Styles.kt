package com.verseviewer.application.app

import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import org.controlsfx.glyphfont.GlyphFontRegistry
import tornadofx.*

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
        val passage by cssclass()
        val passageHeader by cssclass()
        val passageBody by cssclass()
        val frame by cssclass()

        val scheduleSaveButton by cssclass()
        val scheduleLoadButton by cssclass()

        val transparentButton by cssclass()

        val loginListRow by cssclass()
        val loginListSymbol by cssclass()

        val listLabel by cssclass()

        val fontAwesome = GlyphFontRegistry.font("FontAwesome")
    }

    init {

        transparent {
            backgroundColor += c(0, 100, 100, 0.05)
        }

        invisible {
            backgroundColor += c(0, 0, 0, 0.5)
        }

        translationHeader {
            stroke = c(0.0,0.0,0.0)
            fill = c(237, 236, 173)
            strokeWidth = 1.px
        }

        passage {
            text {
                this.stroke = c(0.0, 0.0, 0.0)
                this.strokeWidth = 2.px
                this.fill = c(255, 255, 255)
            }
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


        transparentButton {
            backgroundColor += Color.TRANSPARENT
            backgroundInsets += box(0.px)
            and(hover) {
                backgroundColor += Color.LIGHTGREY
            }
        }

        loginListRow {

            label {
                fontSize = 20.px
                textFill = Color.WHITE
            }

            text {
                strokeWidth = 0.5.px
                stroke = Color.BLACK
                fill = Color.WHITE
                font = Font.font("Arial",  FontWeight.NORMAL, FontPosture.ITALIC, 20.0)
            }

            and(hover) {
                backgroundColor += Color.LIGHTGREY
            }
        }

        loginListSymbol {
            fill = Color.YELLOW
        }

        listLabel {
            fontSize = 35.px
            stroke = c(0.0,0.0,0.0)
            fill = c(255, 255, 255)
            strokeWidth = 0.5.px
        }
    }
}