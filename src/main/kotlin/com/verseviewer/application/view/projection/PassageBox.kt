package com.verseviewer.application.view.projection

import com.verseviewer.application.controller.PassageBoxController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.Passage
import com.verseviewer.application.model.ProjectionModel
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.scene.Group
import javafx.scene.layout.Priority
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.scene.text.TextFlow
import tornadofx.*


class PassageBox : Fragment() {

    private val controller : PassageBoxController by inject()
    private val displayVersesModel : DisplayVersesModel by inject()
    private val projectionModel : ProjectionModel by inject()

    private val passageTexts = mutableListOf<Pair<Text, Text>>()

    private val translationHeader = text()

    private var tf : TextFlow by singleAssign()
    private var rectBound : Rectangle by singleAssign()

    var fs = 50.0
    private val heightPadding = 20.0


    override val root = anchorpane {

        tf = textflow {
            anchorpaneConstraints {
                topAnchor = 0.0
                leftAnchor = 0.0
                rightAnchor = 0.0
                bottomAnchor = 0.0
            }
            maxHeightProperty().bind(this@anchorpane.heightProperty())
            prefHeightProperty().bind(this@anchorpane.heightProperty())
            paddingAll = 10.0
            textAlignment = TextAlignment.CENTER

            sceneProperty().addListener {_, _, new ->
                new?.let { resizeTexts(this) }
            }
        }

        rectBound = Rectangle().apply {
            heightProperty().bind(this@anchorpane.heightProperty())
            widthProperty().bind(this@anchorpane.widthProperty())
        }

        projectionModel.font.value = Font.font(50.0)
        vboxConstraints { vGrow = Priority.ALWAYS }
        hboxConstraints { hGrow = Priority.ALWAYS }
    }



    init {

        displayVersesModel.itemProperty.addListener { _, _, new ->
            if (new != null) {
                fs = 50.0
                projectionModel.font.value = Font.font(fs)
                val passages = displayVersesModel.sorted[0]
                translationHeader.text = passages.first().translation.abbreviation
                rebuildTexts(passages)
            }
        }
    }


    private fun rebuildTexts(list : List<Passage>) {
        runAsync {
            controller.buildTexts(list)
        } ui {
            rebuildUI(it)
        }
    }

    private fun resizeTexts(tf : TextFlow) {
        if (tf.children.size >= 2) {
            var textHeight = 0.0
            tf.children.filterIsInstance<Text>().forEach {
                textHeight += it.layoutBounds.height
            }
            while (textHeight > tf.height) {
                fs -= 1
                projectionModel.font.value = Font.font(fs)
                textHeight = 0.0
                tf.layout()
                tf.children.filterIsInstance<Text>().forEach {
                    it.applyCss()
                    textHeight += it.layoutBounds.height
                }
            }
        }
    }

    private fun rebuildUI(list: List<Pair<String, String>> = controller.passageStrings.toList()) {
        root.children.clear()
        tf.clear()
        list.forEach {
            val header = Text(it.first + "\n").apply {
                fontProperty().bind(projectionModel.font)
            }
            val body = Text(it.second).apply {
                fontProperty().bind(projectionModel.font)
            }
            tf.add(header)
            tf.add(body)
        }
        root.add(tf)
    }
}