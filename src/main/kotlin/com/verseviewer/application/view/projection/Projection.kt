package com.verseviewer.application.view.projection

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.ProjectionController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.ProjectionModel
import com.verseviewer.application.model.event.*
import javafx.animation.FadeTransition
import javafx.geometry.Orientation
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.StageStyle
import javafx.util.Duration
import tornadofx.*

class Projection : Fragment() {

    private val displayVersesModel : DisplayVersesModel by inject()
    private val controller : ProjectionController by inject();
    private val projectionModel : ProjectionModel by inject()

    private val fadeDuration = Duration.millis(500.0)
    private var fadeOutTransition by singleAssign<FadeTransition>()

    private var lastNumTranslations = 0
    private var isCloseable : Boolean by singleAssign()

    private var hbox : HBox by singleAssign()
    private var vbox : VBox by singleAssign()


    override val root = anchorpane {

        opacity = 0.0
        addClass(Styles.invisible)

        fadeOutTransition = FadeTransition(fadeDuration, this).apply {
            fromValueProperty().bind(opacityProperty())
            toValue = 0.0
            setOnFinished { if (isCloseable) currentStage?.close() }
            delay = Duration.seconds(2.0)
        }

        subscribe<OpenProjection> {
            fadeOutTransition.stop()
            opacityProperty().animate(1.0, fadeDuration)
            fire(PlayFrameAnimation(scope))
        }

        subscribe<CloseProjection> {
            fadeOutTransition.playFromStart()
            fire(PlayReverseFrameAnimation(scope))
        }

        hbox = HBox().apply {
            anchorpaneConstraints {
                topAnchor = 0.0
                leftAnchor = 0.0
                rightAnchor = 0.0
                bottomAnchor= 0.0
            }
        }

        vbox = VBox().apply {
            anchorpaneConstraints {
                topAnchor = 0.0
                leftAnchor = 0.0
                rightAnchor = 0.0
                bottomAnchor= 0.0
            }
        }

        when (projectionModel.orientation) {
            Orientation.HORIZONTAL -> add(hbox)
            Orientation.VERTICAL -> add(vbox)
        }

        projectionModel.orientationProperty.onChange {
            if (it != null) {
                when (it) {
                    Orientation.HORIZONTAL -> {
                        children.remove(vbox)
                        children.add(hbox)
                    }
                    Orientation.VERTICAL -> {
                        children.remove(hbox)
                        children.add(vbox)
                    }
                }
                project(it, true)
            }
        }

        projectionModel.screenBoundsProperty.addListener { _, _, new ->
            if (new != null) {
                projectionModel.boxWidthProperty.value = new.width / displayVersesModel.sorted.value.size
                projectionModel.boxHeightProperty.value = new.height
                fire(InitAfterBoundsSet())
            }
        }

        displayVersesModel.itemProperty.addListener { _, _, new ->
            if (new != null) {
                project(projectionModel.orientation, lastNumTranslations != displayVersesModel.sorted.value.size)
            }
        }
    }

    init {
        isCloseable = params["isCloseable"] as? Boolean ?: true
    }

    override fun onDock() {
        initStageSettings()
    }

    private fun initStageSettings() {
        currentStage?.scene?.fill = null
        modalStage?.isMaximized = true
        modalStage?.isResizable = false
        modalStage?.x = projectionModel.screenBoundsProperty.value.minX
        modalStage?.y = projectionModel.screenBoundsProperty.value.minY
    }

    private fun project(layout : Orientation?, rebuildBoxes : Boolean) {
        if (rebuildBoxes) {
            println("Rebuild")
            fire(PlayReverseFrameAnimation(scope))
            initStageSettings()
            buildPassageBoxes(displayVersesModel.sorted.value.size, layout)
            fire(InitAfterBoundsSet())
            lastNumTranslations = displayVersesModel.sorted.value.size
            fire(PlayFrameAnimation(scope))
        }
        fire(BuildPassageContent())
    }

    private fun buildPassageBoxes(currentSize : Int, layout : Orientation?) {
        if (layout != null) {
            var calcWidth = projectionModel.screenBoundsProperty.value.width
            var calcHeight = projectionModel.screenBoundsProperty.value.height
            when (layout) {
                Orientation.VERTICAL -> calcHeight /= currentSize
                Orientation.HORIZONTAL -> calcWidth /= currentSize
            }
            projectionModel.boxWidth = calcWidth
            projectionModel.boxHeight = calcHeight

            when (layout) {
                Orientation.VERTICAL -> {
                    vbox.children.clear()
                    for (i in 0 until currentSize)
                        vbox.add(PassageBox::class, mapOf("translationIndex" to i))
                }
                Orientation.HORIZONTAL -> {
                    hbox.children.clear()
                    for (i in 0 until currentSize)
                        hbox.add(PassageBox::class, mapOf("translationIndex" to i))
                }
            }
        }
    }
}