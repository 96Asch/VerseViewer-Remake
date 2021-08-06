package com.verseviewer.application.view.components.versebox

import com.verseviewer.application.controller.DBController
import com.verseviewer.application.model.SpecialSymbolModel
import com.verseviewer.application.model.PassageModel
import com.verseviewer.application.model.event.RefreshList
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.geometry.Side
import javafx.scene.control.TextArea
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import tornadofx.*
import tornadofx.controlsfx.masterdetailpane

class VerseEditor : Fragment() {
    private val passageModel : PassageModel by inject()
    private val symbolModel : SpecialSymbolModel by inject()
    private val dbController : DBController by inject()
    private val stView : SpecialSymbolTable by inject()

    override val root = borderpane {

        val glyph = GlyphFontRegistry.font("FontAwesome")
        left = vbox {
            isFillWidth = true
            button(graphic = glyph.create(FontAwesome.Glyph.SAVE)) {
                enableWhen(passageModel.dirty)
                setOnAction(::save)
                shortcut("Ctrl+S")
            }
            button(graphic = glyph.create(FontAwesome.Glyph.UNDO)) {
                setOnAction(::save)
            }
            togglebutton {
                graphic = glyph.create(FontAwesome.Glyph.ASTERISK)
//                master.showDetailNodeProperty().bind(selectedProperty())
                isSelected = false
                shortcut("Ctrl+Y") {
                    this.isSelected = !this.isSelected
                }
            }
        }

    }

    override fun onUndock() {
        fire(RefreshList(listOf(passageModel.item)))
    }

    private fun cancel(evt : ActionEvent) {
        passageModel.rollback()
        close()
    }

    private fun save(evt: ActionEvent) {
        passageModel.commit()
        dbController.updateVerseText(passageModel.item)
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
