package com.verseviewer.application.view.projection

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.PassageBoxController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.FontModel
import com.verseviewer.application.model.Passage
import com.verseviewer.application.model.ProjectionModel
import com.verseviewer.application.model.event.*
import com.verseviewer.application.util.NodeUtils
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.Priority
import javafx.scene.text.*
import tornadofx.*


class PassageBox : Fragment() {

    private val controller : PassageBoxController by inject()
    private val displayVersesModel : DisplayVersesModel by inject()
    private val projectionModel : ProjectionModel by inject()
    private val fontModel : FontModel by inject()

    private var bodyTextFlow : TextFlow by singleAssign()
    private var headerText : Text by singleAssign()
    private val bodyFontProperty = SimpleObjectProperty<Font>()
    private val headerFontProperty = SimpleObjectProperty<Font>()

    private var lastTranslationName = ""

    private var headerFontSize = 0.0
    private var bodyFontSize = 0.0
    private val heightMargin = 30.0
    private val frameHeightMargin = 30.0
    private val topLineFactor = 2.0


    val textFlowMarginProperty = SimpleDoubleProperty(10.0)
    var textflowMargin by textFlowMarginProperty

    private val bodyWidth = projectionModel.boxWidth.toDouble() - 50.0
    private val headerWidth = ((projectionModel.boxWidth.toDouble() - textflowMargin*2) / topLineFactor) -  (60.0)

    override val root = anchorpane {

        val boxHeight = projectionModel.boxHeight.toDouble()
        val boxWidth = projectionModel.boxWidth.toDouble()

        val frame = Frame(boxWidth, boxHeight).apply {
            subscribe<PlayFrameAnimation> {
                this@apply.playFromStart()
            }
            subscribe<PlayReverseFrameAnimation> {
                this@apply.reversePlay()
            }
        }

        this += frame

        headerText = text {
            anchorpaneConstraints {
                topAnchor = 5.0
                leftAnchor = textflowMargin + 40.0
            }
            addClass(Styles.translationHeader)
            fontProperty().bind(headerFontProperty)
            maxWidth = boxWidth / 2.5
            textAlignment = TextAlignment.CENTER
            frame.buildHeaderFadeTransition(this)
        }

        bodyTextFlow = textflow {
            anchorpaneConstraints {
                topAnchor = textflowMargin + heightMargin + frameHeightMargin
                leftAnchor = textflowMargin
                rightAnchor = textflowMargin

            }
            textAlignmentProperty().bind(projectionModel.textAlignmentProperty)
            paddingLeftProperty.bind(textFlowMarginProperty)
            paddingRightProperty.bind(textFlowMarginProperty)
            maxHeight = boxHeight - textflowMargin - heightMargin - frameHeightMargin - 30.0
            frame.buildBodyFadeTransition(this)
        }

        subscribe<ScaleUI> {
            bodyTextFlow.children.forEach { node ->
                node.scaleX = it.scaleX
                node.scaleY = it.scaleY
            }

            headerText.apply {
                scaleX = it.scaleX
                scaleY = it.scaleY
            }
        }

        subscribe<InitAfterBoundsSet> {
            frame.buildSimpleSequentialAnimation(textflowMargin, frameHeightMargin, topLineFactor)
            frame.initAnimation()
        }

        maxWidth = boxWidth
        maxHeight = boxHeight
        prefWidth = boxWidth
        prefHeight = boxHeight
        vboxConstraints { vGrow = Priority.ALWAYS }
        hboxConstraints { hGrow = Priority.ALWAYS }

        fontModel.familyProperty.onChange {
            fire(BuildPassageContent())
        }
        fontModel.sizeProperty.onChange {
            if (it != null) {
                headerFontSize = it.toDouble()
                bodyFontSize = it.toDouble()
            }
            fire(BuildPassageContent())
        }
        fontModel.weightProperty.onChange {
            fire(BuildPassageContent())
        }
        fontModel.postureProperty.onChange {
            fire(BuildPassageContent())
        }
    }

    init {
        val translationIndex = params["translationIndex"] as? Int ?: 0
        setFont(headerFontProperty, 50.0)

        subscribe<BuildPassageContent> {
            headerFontSize = fontModel.size.toDouble()
            bodyFontSize = fontModel.size.toDouble()
            setFont(bodyFontProperty, bodyFontSize)
            val index = if (translationIndex >= displayVersesModel.sorted.value.size) 0 else translationIndex
            rebuildTexts(displayVersesModel.sorted.value[index])
        }
    }

    private fun rebuildTexts(list : List<Passage>) {
        runAsync {
            controller.buildTexts(list)
        } ui {
            val translationName = if (displayVersesModel.sorted.value.size > 3)
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

    private fun rebuildHeader(translationString : String) {
        root.children.remove(headerText)
        resizeHeaderTextFlow(translationString)
        setFont(headerFontProperty, headerFontSize)
        headerText.text = translationString
        root.add(headerText)
    }

    private fun rebuildBody(list: List<Pair<String, String>> = controller.passageStrings.toList()) {
        root.children.remove(bodyTextFlow)
        bodyTextFlow.clear()
        resizeBodyTextFlow(list)
        setFont(bodyFontProperty, bodyFontSize)

        list.forEachIndexed { i, it ->
            val header = Text(it.first + "\n").apply {
                fontProperty().bind(bodyFontProperty)
                addClass(Styles.passageHeader)
            }
            val body = Text(it.second).apply {
                fontProperty().bind(bodyFontProperty)
                addClass(Styles.passageBody)
            }
            bodyTextFlow.add(header)
            bodyTextFlow.add(body)

            if (i+1 < list.size) {
                bodyTextFlow.add(Text(System.lineSeparator()))
            }
        }
        root.add(bodyTextFlow)
    }

    private fun resizeHeaderTextFlow(header : String = headerText.text) {
        var headerFont = Font.font(fontModel.family, fontModel.weight, fontModel.posture, headerFontSize)

        var width = NodeUtils.computeTextWidth(headerFont, header, 0.0)
        while (width > headerWidth) {
            headerFontSize -= 1.0
            headerFont = Font.font(fontModel.family, fontModel.weight, fontModel.posture, headerFontSize)
            width = NodeUtils.computeTextWidth(headerFont, header, 0.0)
        }
    }

    private fun resizeBodyTextFlow(list: List<Pair<String, String>> = controller.passageStrings) {
        var height = 0.0
        var bodyFont = Font.font(fontModel.family, fontModel.weight, fontModel.posture, bodyFontSize)

        list.forEach {
            height += NodeUtils.computeTextHeight(bodyFont, it.first, bodyWidth)
            height += NodeUtils.computeTextHeight(bodyFont, it.second, bodyWidth)
        }

        while (height > bodyTextFlow.maxHeight) {
            bodyFontSize -= 0.50
            bodyFont = Font.font(fontModel.family, fontModel.weight, fontModel.posture, bodyFontSize)
            list.forEach {
                height = NodeUtils.computeTextHeight(bodyFont, it.first, bodyWidth)
                height += NodeUtils.computeTextHeight(bodyFont, it.second, bodyWidth)
            }
        }
    }



    private fun setFont(property : SimpleObjectProperty<Font>, size : Double) {
        property.value = Font.font(fontModel.family, fontModel.weight, fontModel.posture, size)
    }
}