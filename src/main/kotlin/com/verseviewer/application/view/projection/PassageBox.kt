package com.verseviewer.application.view.projection

import com.verseviewer.application.controller.PassageBoxController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.Passage
import com.verseviewer.application.model.datastructure.VerseGroup
import javafx.scene.Parent
import tornadofx.Fragment
import tornadofx.anchorpane

class PassageBox : Fragment() {

    private val controller : PassageBoxController by inject()
    private val displayVersesModel : DisplayVersesModel by inject()

    override val root = anchorpane {

    }

    init {

        displayVersesModel.itemProperty.addListener { _, _, new ->
            if (new != null) {
                rebuildUI(displayVersesModel.sorted[0])
            }
        }
    }

    private fun rebuildUI(list : List<Passage>) {
        controller.buildText(list)
        root.children.clear()
        controller.passageTexts.forEach {
            root.children.add(it.first)
            root.children.add(it.second)
        }
    }
}