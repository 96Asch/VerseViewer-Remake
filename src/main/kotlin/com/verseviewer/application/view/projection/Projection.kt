package com.verseviewer.application.view.projection

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.ProjectionController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.ProjectionModel
import com.verseviewer.application.model.event.CloseProjection
import com.verseviewer.application.model.event.InitAfterBoundsSet
import com.verseviewer.application.model.event.OpenProjection
import javafx.animation.FadeTransition
import javafx.beans.property.SimpleDoubleProperty
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


    override val root = anchorpane {

        opacity = 0.0
        addClass(Styles.invisible)

        fadeOutTransition = FadeTransition(fadeDuration, this).apply {
            fromValueProperty().bind(opacityProperty())
            toValue = 0.0
//            setOnFinished { currentStage?.close() }
            delay = Duration.seconds(1.0)
        }

        subscribe<OpenProjection> {
            fadeOutTransition.stop()
            opacityProperty().animate(1.0, fadeDuration)
        }

        subscribe<CloseProjection> {
            fadeOutTransition.playFromStart()
        }

        hbox {
            anchorpaneConstraints {
                topAnchor = 0.0
                leftAnchor = 0.0
                rightAnchor = 0.0
                bottomAnchor= 0.0
            }
            maxWidthProperty().bind(screenWidthProperty)
            maxHeightProperty().bind(screenHeightProperty)

            add<PassageBox>()

            screenWidthProperty.addListener { _, _, _ ->
                fire(InitAfterBoundsSet())
            }
        }
    }

    override fun onDock() {
        currentStage?.scene?.fill = null
        modalStage?.isMaximized = true
        modalStage?.isResizable = false
        val screenBounds = Screen.getScreens()[projectionModel.displayIndex.value].visualBounds
        screenWidthProperty.value = screenBounds.width
        screenHeightProperty.value = screenBounds.height
        modalStage?.x = screenBounds.minX
        modalStage?.y = screenBounds.minY
    }

}