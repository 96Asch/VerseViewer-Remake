package com.example.demo.view.versebox

import com.example.demo.controller.DBController
import com.example.demo.model.SpecialSymbolModel
import com.example.demo.model.VerseModel
import com.example.demo.model.datastructure.GroupType
import com.example.demo.model.event.RefreshList
import javafx.beans.value.ChangeListener
import javafx.geometry.Side
import javafx.scene.control.TextArea
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

        left = vbox {
            isFillWidth = true
            button("Save") {
                enableWhen(verseModel.dirty)
                action {
                    save()
                }
                shortcut("Ctrl+S")
                maxWidth = Double.MAX_VALUE
            }
            button("Cancel") {
                action {
                    cancel()
                }
                maxWidth = Double.MAX_VALUE
            }
            toggleswitch ("Symbols", master.showDetailNodeProperty()) {
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

    private fun cancel() {
        verseModel.rollback()
        close()
    }

    private fun save() {
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
