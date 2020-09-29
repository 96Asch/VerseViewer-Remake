package com.verseviewer.application.view.projection

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.ProjectionController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.ProjectionModel
import com.verseviewer.application.model.event.*
import javafx.animation.FadeTransition
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Screen
import javafx.util.Duration
import tornadofx.*

class Projection : View() {

    private val displayVersesModel : DisplayVersesModel by inject()
    private val controller : ProjectionController by inject();
    private val projectionModel : ProjectionModel by inject()

    private val screenWidthProperty = SimpleDoubleProperty()
    private val screenHeightProperty = SimpleDoubleProperty()

    private val fadeDuration = Duration.millis(500.0)
    private var fadeOutTransition by singleAssign<FadeTransition>()

    private var lastNumTranslations = 0

    private var hbox : HBox by singleAssign()
    private var vbox : VBox by singleAssign()


    override val root = anchorpane {

        opacity = 0.0
        addClass(Styles.invisible)

        fadeOutTransition = FadeTransition(fadeDuration, this).apply {
            fromValueProperty().bind(opacityProperty())
            toValue = 0.0
            setOnFinished { currentStage?.close() }
            delay = Duration.seconds(2.0)
        }

        subscribe<OpenProjection> {
            fadeOutTransition.stop()
            opacityProperty().animate(1.0, fadeDuration)
            fire(PlayFrameAnimation())
        }

        subscribe<CloseProjection> {
            fadeOutTransition.playFromStart()
            fire(PlayReverseFrameAnimation())
        }

        hbox = hbox {
            anchorpaneConstraints {
                topAnchor = 0.0
                leftAnchor = 0.0
                rightAnchor = 0.0
                bottomAnchor= 0.0
            }
        }
        maxWidthProperty().bind(screenWidthProperty)
        maxHeightProperty().bind(screenHeightProperty)

        this += hbox
        screenWidthProperty.addListener { _, _, _ ->
            fire(InitAfterBoundsSet())
        }

        displayVersesModel.itemProperty.addListener { _, _, new ->
            if (new != null) {
                if (lastNumTranslations != displayVersesModel.sorted.size) {
                    fire(PlayReverseFrameAnimation())
                    initStageSettings()
                    buildPassageBoxes(displayVersesModel.sorted.size, hbox)
                    fire(InitAfterBoundsSet())
                    lastNumTranslations = displayVersesModel.sorted.size
                    fire(PlayFrameAnimation())
                }
                fire(BuildPassageContent())
            }
        }
    }

    override fun onDock() {
        initStageSettings()
    }

    private fun initStageSettings() {
        currentStage?.scene?.fill = null
        modalStage?.isMaximized = true
        modalStage?.isResizable = false
        val screenBounds = Screen.getScreens()[projectionModel.displayIndex.value].visualBounds
        screenWidthProperty.value = screenBounds.width
        screenHeightProperty.value = screenBounds.height
        modalStage?.x = screenBounds.minX
        modalStage?.y = screenBounds.minY
    }

    private fun buildPassageBoxes(currentSize : Int, container : Pane) {
        projectionModel.width.value = screenWidthProperty.value / currentSize
        projectionModel.height.value = screenHeightProperty.value
        container.children.clear()
        for (i in 0 until currentSize)
            container.add(PassageBox::class, mapOf("translationIndex" to i))
    }
}

enum class BoxLayout {
    HORIZONTAL,
    VERTICAL
}