package com.verseviewer.application.view.projection

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.PassageBoxController
import com.verseviewer.application.model.*
import com.verseviewer.application.model.event.*
import com.verseviewer.application.util.NodeUtils
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.CacheHint
import javafx.scene.layout.Priority
import javafx.scene.text.*
import tornadofx.*


class PassageBox : Fragment() {

    private val controller : PassageBoxController by inject()

    private val verseGroupModel : VerseGroupModel by inject()
    private val preferenceModel : SnapshotModel by inject()
    private val projectionModel : ProjectionModel by inject()

    private var bodyTextFlow : TextFlow by singleAssign()
    private var headerText : Text by singleAssign()
    private val bodyFontProperty = SimpleObjectProperty<Font>()
    private val headerFontProperty = SimpleObjectProperty<Font>()

    private var lastTranslationName = ""
    private var headerFontSize = 0.0
    private var bodyFontSize = 0.0

    private val heightMargin = 10.0
    private val frameHeightMargin = 30.0
    private val topLineFactor = 2.0

    private val textFlowMarginProperty = SimpleDoubleProperty(10.0)
    private var textflowMargin by textFlowMarginProperty

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

        headerText = Text().apply {
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

        bodyTextFlow = TextFlow().apply {
            anchorpaneConstraints {
                topAnchor = textflowMargin + heightMargin + frameHeightMargin
                leftAnchor = textflowMargin
                rightAnchor = textflowMargin

            }
            addClass(Styles.passage)
            isCache = true
            isCacheShape= true
            cacheHint = CacheHint.SPEED
            textAlignmentProperty().bind(preferenceModel.textAlignmentProperty)
            paddingLeftProperty.bind(textFlowMarginProperty)
            paddingRightProperty.bind(textFlowMarginProperty)
            maxHeight = boxHeight - textflowMargin - heightMargin - frameHeightMargin - 30.0
            frame.buildBodyFadeTransition(this)
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

        preferenceModel.familyProperty.onChange {
            fire(BuildPassageContent())
        }
        preferenceModel.sizeProperty.onChange {
            if (it != null) {
                headerFontSize = it.toDouble()
                bodyFontSize = it.toDouble()
            }
            fire(BuildPassageContent())
        }
        preferenceModel.weightProperty.onChange {
            fire(BuildPassageContent())
        }
        preferenceModel.postureProperty.onChange {
            fire(BuildPassageContent())
        }
    }

    init {
        val translationIndex = params["translationIndex"] as? Int ?: 0
        setFont(headerFontProperty, 50.0)

        subscribe<BuildPassageContent> {
            headerFontSize = preferenceModel.size.toDouble()
            bodyFontSize = preferenceModel.size.toDouble()
            setFont(bodyFontProperty, bodyFontSize)
            val index = if (translationIndex >= verseGroupModel.sorted.value.size) 0 else translationIndex
            rebuildTexts(verseGroupModel.sorted.value[index])
        }
    }

    private fun rebuildTexts(list : List<Passage>) {
        runAsync {
            controller.buildTexts(list)
        } ui {
            val translationName = if (verseGroupModel.sorted.value.size > 3)
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
                fillProperty().bind(preferenceModel.fillProperty)
                strokeProperty().bind(preferenceModel.strokeProperty)
                strokeWidthProperty().bind(preferenceModel.strokeWidthProperty)
            }
            val body = Text(it.second).apply {
                fontProperty().bind(bodyFontProperty)
                fillProperty().bind(preferenceModel.fillProperty)
                strokeProperty().bind(preferenceModel.strokeProperty)
                strokeWidthProperty().bind(preferenceModel.strokeWidthProperty)
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
        var headerFont = Font.font(preferenceModel.family, preferenceModel.weight, preferenceModel.posture, headerFontSize)
        var width = NodeUtils.computeTextWidth(headerFont, header, 0.0)
        while (width > headerWidth && headerFontSize >= 10) {
            headerFontSize -= 1.0
            headerFont = Font.font(preferenceModel.family, preferenceModel.weight, preferenceModel.posture, headerFontSize)
            width = NodeUtils.computeTextWidth(headerFont, header, 0.0)
        }
    }

    private fun resizeBodyTextFlow(list: List<Pair<String, String>> = controller.passageStrings) {
        var height = 0.0
        var bodyFont = Font.font(preferenceModel.family, preferenceModel.weight, preferenceModel.posture, bodyFontSize)

        list.forEach {
            height += NodeUtils.computeTextHeight(bodyFont, it.first, bodyWidth)
            height += NodeUtils.computeTextHeight(bodyFont, it.second, bodyWidth)
        }

        while (height > bodyTextFlow.maxHeight && bodyFontSize >= 10) {
            bodyFontSize -= 0.50
            bodyFont = Font.font(preferenceModel.family, preferenceModel.weight, preferenceModel.posture, bodyFontSize)
            height = 0.0
            list.forEach {
                height += NodeUtils.computeTextHeight(bodyFont, it.first, bodyWidth)
                height += NodeUtils.computeTextHeight(bodyFont, it.second, bodyWidth)
            }
        }
    }

    private fun setFont(property : SimpleObjectProperty<Font>, size : Double) {
        property.value = Font.font(preferenceModel.family, preferenceModel.weight, preferenceModel.posture, size)
    }
}