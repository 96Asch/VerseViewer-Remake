package com.verseviewer.application.view.projection

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.PassageBoxController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.Passage
import com.verseviewer.application.model.ProjectionModel
import com.verseviewer.application.model.event.*
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.Priority
import javafx.scene.text.*
import tornadofx.*


class PassageBox : Fragment() {

    private val controller : PassageBoxController by inject()
    private val displayVersesModel : DisplayVersesModel by inject()
    private val projectionModel : ProjectionModel by inject()

    private var tf : TextFlow by singleAssign()
    private var headerTf : TextFlow by singleAssign()
    private val bodyFontProperty = SimpleObjectProperty<Font>()
    private val headerFontProperty = SimpleObjectProperty<Font>()

    private var lastTranslationName = ""

    private var headerFontSize = 0.0
    private var bodyFontSize = 0.0
    private val heightMargin = 40.0
    private val frameHeightMargin = 30.0



    val textFlowMarginProperty = SimpleDoubleProperty(10.0)
    var textflowMargin by textFlowMarginProperty

    override val root = anchorpane {

        val boxHeight = projectionModel.height.value
        val boxWidth = projectionModel.width.value

        println("$boxWidth - $boxHeight")

        val frame = Frame(boxWidth, boxHeight).apply {
            subscribe<PlayFrameAnimation> {
                this@apply.playFromStart()
            }

            subscribe<PlayReverseFrameAnimation> {
                this@apply.reversePlay()
            }
        }

        this += frame

        headerTf = textflow {
            anchorpaneConstraints {
                topAnchor = textflowMargin
                leftAnchor = textflowMargin + 10.0
            }
            maxHeight = boxHeight/ 15
            paddingTop = -10.0
            prefWidth = (boxWidth/ 1.5) - textflowMargin
            textAlignment = TextAlignment.CENTER
            frame.buildHeaderFadeTransition(this)
            this.sceneProperty().addListener { _, _, new ->
                new?.let { resizeHeaderTextFlow(this) }
            }
        }

        tf = textflow {
            anchorpaneConstraints {
                topAnchor = textflowMargin + heightMargin + frameHeightMargin
                leftAnchor = textflowMargin
                rightAnchor = textflowMargin

            }
            paddingLeftProperty.bind(textFlowMarginProperty)
            paddingRightProperty.bind(textFlowMarginProperty)
            maxHeight = boxHeight - textflowMargin - heightMargin - frameHeightMargin - 10.0
            textAlignment = TextAlignment.LEFT
            this.sceneProperty().addListener { _, _, new ->
                new?.let { resizeBodyTextFlow(this) }
            }
            frame.buildBodyFadeTransition(this)
        }

        subscribe<InitAfterBoundsSet> {
            frame.buildSimpleSequentialAnimation(textflowMargin, frameHeightMargin)
            frame.initAnimation()
        }

        maxWidth = boxWidth
        maxHeight = boxHeight
        prefWidth = boxWidth
        prefHeight = boxHeight
        vboxConstraints { vGrow = Priority.ALWAYS }
        hboxConstraints { hGrow = Priority.ALWAYS }
    }

    init {
        projectionModel.font.value = Font.font(60.0)
        val translationIndex = params["translationIndex"] as? Int ?: 0
        setFont(headerFontProperty, 80.0)


        subscribe<BuildPassageContent> {
            headerFontSize = projectionModel.font.value.size
            bodyFontSize = projectionModel.font.value.size
            setFont(bodyFontProperty, bodyFontSize)
            val index = if (translationIndex >= displayVersesModel.sorted.size) 0 else translationIndex
            val passages = displayVersesModel.sorted[index]
            rebuildTexts(passages)
        }
    }

    private fun rebuildTexts(list : List<Passage>) {
        runAsync {
            controller.buildTexts(list)
        } ui {
            val translationName = if (displayVersesModel.sorted.size > 3)
                list.first().translation.abbreviation
            else
                list.first().translation.name
            if (lastTranslationName != translationName) {
                rebuildHeader(translationName)
                lastTranslationName = translationName
            }
            rebuildBody(it)
        }
    }

    private fun resizeBodyTextFlow(tf : TextFlow) {
        var textHeight = 0.0
        tf.children.filterIsInstance<Text>().forEach {
            textHeight += it.layoutBounds.height
        }
        println("$textHeight > ${tf.maxHeight}")
        while (tf.maxHeight > 0 && textHeight > tf.maxHeight) {
            bodyFontSize -= 0.50
            setFont(bodyFontProperty, bodyFontSize)
            textHeight = 0.0
            tf.layout()
            tf.children.filterIsInstance<Text>().forEach {
                it.applyCss()
                textHeight += it.layoutBounds.height
            }
        }
    }

    private fun resizeHeaderTextFlow(tf : TextFlow) {
        var textHeight = 0.0
        tf.children.filterIsInstance<Text>().forEach {
            textHeight += it.layoutBounds.height
        }

        while (tf.maxHeight > 0 && textHeight > tf.maxHeight) {
            headerFontSize -= 0.50
            setFont(headerFontProperty, headerFontSize)
            textHeight = 0.0
            tf.layout()
            tf.children.filterIsInstance<Text>().forEach {
                it.applyCss()
                textHeight += it.layoutBounds.height
            }
        }
    }

    private fun rebuildHeader(translationString : String) {
        root.children.remove(headerTf)
        headerTf.clear()
        val translationHeader = Text(translationString).apply {
            fontProperty().bind(headerFontProperty)
            addClass(Styles.passageHeader)
        }
        headerTf.add(translationHeader)
        root.add(headerTf)
    }

    private fun rebuildBody(list: List<Pair<String, String>> = controller.passageStrings.toList()) {
        root.children.remove(tf)
        tf.clear()
        list.forEachIndexed { i, it ->
            val header = Text(it.first + "\n").apply {
                fontProperty().bind(bodyFontProperty)
                addClass(Styles.passageHeader)
            }
            val body = Text(it.second).apply {
                fontProperty().bind(bodyFontProperty)
                addClass(Styles.passageBody)
            }
            tf.add(header)
            tf.add(body)

            if (i+1 < list.size) {
                tf.add(Text(System.lineSeparator()))
            }
        }
        root.add(tf)
    }

    private fun setFont(property : SimpleObjectProperty<Font>, size : Double) {
        val font = Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, size)
        property.value = font
    }
}