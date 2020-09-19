package com.verseviewer.application.view.projection

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.PassageBoxController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.Passage
import com.verseviewer.application.model.ProjectionModel
import com.verseviewer.application.model.datastructure.VerseGroup
import javafx.scene.Parent
import javafx.scene.layout.Priority
import javafx.scene.text.TextFlow
import tornadofx.*

class PassageBox : Fragment() {

    private val controller : PassageBoxController by inject()
    private val displayVersesModel : DisplayVersesModel by inject()
    private val projectionModel : ProjectionModel by inject()

    private val translationHeader = text()
    private val textFlow = textflow()

    override val root = anchorpane {

        this += textFlow.apply {
            
            prefWidthProperty().bind(this@anchorpane.widthProperty().subtract(projectionModel.textPadding.value))
            prefHeightProperty().bind(this@anchorpane.heightProperty().subtract(projectionModel.textPadding.value))
        }
        controller.containerWidthProperty.bind(widthProperty())

        vboxConstraints { vGrow = Priority.ALWAYS }
        hboxConstraints { hGrow = Priority.ALWAYS }
        addClass(Styles.anchorPaneTest)
    }

    init {

        displayVersesModel.itemProperty.addListener { _, _, new ->
            if (new != null) {
                val passages = displayVersesModel.sorted[0]
                translationHeader.text = passages.first().translation.abbreviation
                rebuildUI(passages)
            }
        }
    }

    private fun rebuildUI(list : List<Passage>) {
        controller.buildText(list)
        root.children.removeIf {it !is TextFlow}
        controller.uiElements.forEach {
            root.children.add(it)
        }
        textFlow.clear()
        controller.passageTexts.forEach {
            textFlow.add(it.first)
            textFlow.add(it.second)
        }
    }
}