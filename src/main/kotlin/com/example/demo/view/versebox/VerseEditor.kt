package com.example.demo.view.versebox

import com.example.demo.controller.DBController
import com.example.demo.model.SpecialSymbolModel
import com.example.demo.model.VerseModel
import com.example.demo.model.datastructure.GroupType
import com.example.demo.model.event.RefreshList
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.geometry.Side
import javafx.scene.control.TextArea
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import tornadofx.*
import tornadofx.controlsfx.*

class VerseEditor : Fragment() {
    private val verseModel : VerseModel by inject()
    private val symbolModel : SpecialSymbolModel by inject()
    private val dbController : DBController by inject()
    private val stView : SpecialSymbolTable by inject()

    override val root = borderpane {
        val master = masterdetailpane {
            val ta = textarea(verseModel.text) {
                isWrapText = true
                focusedProperty().addListener { _, _, new ->
                    if (new.not()) {
                        symbolModel.caretPos = caretPosition
                    }
                }
            }

            masterNode = ta

            detailNode =  scrollpane {
                this += stView
                isFitToWidth = true
            }

            isShowDetailNode = true
            detailSide = Side.BOTTOM
            symbolModel.symbol.addListener(insertCharListener(ta))
        }

        center = master

        val glyph = GlyphFontRegistry.font("FontAwesome")
        left = vbox {
            isFillWidth = true
            button(graphic = glyph.create(FontAwesome.Glyph.SAVE)) {
                enableWhen(verseModel.dirty)
                setOnAction(::save)
                shortcut("Ctrl+S")
            }
            button(graphic = glyph.create(FontAwesome.Glyph.UNDO)) {
                setOnAction(::save)
            }
            togglebutton {
                graphic = glyph.create(FontAwesome.Glyph.ASTERISK)
                master.showDetailNodeProperty().bind(selectedProperty())
                isSelected = false
                shortcut("Ctrl+Y") {
                    this.isSelected = !this.isSelected
                }
            }
        }

    }

    override fun onUndock() {
        fire(RefreshList(listOf(verseModel.item), GroupType.MONO_TRANSLATION))
    }

    private fun cancel(evt : ActionEvent) {
        verseModel.rollback()
        close()
    }

    private fun save(evt: ActionEvent) {
        verseModel.commit()
        dbController.updateVerseText(verseModel.item)
        close()
    }

    private fun insertCharListener(textArea : TextArea) : ChangeListener<Char> = ChangeListener() { _, _, new ->
        if (new !=  ' ') {
            val sb = StringBuilder(textArea.text)
            sb.insert(symbolModel.caretPos, new)
            textArea.text = sb.toString()
            symbolModel.symbol.value = ' '
        }
    }

}
