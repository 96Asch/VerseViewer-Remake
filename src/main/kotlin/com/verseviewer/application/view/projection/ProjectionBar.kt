package com.verseviewer.application.view.projection

import com.verseviewer.application.model.*
import com.verseviewer.application.model.event.CloseProjection
import com.verseviewer.application.model.event.OpenProjection
import javafx.geometry.Orientation
import javafx.scene.layout.Priority
import javafx.stage.Screen
import javafx.stage.StageStyle
import tornadofx.*

class ProjectionBar : Fragment() {

    private val projectionModel : ProjectionModel by inject()
    private val projection = find<Projection>(mapOf("isCloseable" to true))
    private val displayVersesModel : DisplayVersesModel by inject()
    private val fontModel : FontModel by inject()

    private val liveChangeListener = ChangeListener<Boolean> { _, _, new ->
        if (new) {
            projection.openWindow(StageStyle.TRANSPARENT, escapeClosesWindow = false)
            fire(OpenProjection(scope))
        }
        else {
            fire(CloseProjection(scope))
        }
        projectionModel.isLive = new
    }


    override val root = hbox {
        projectionModel.screenBounds = Screen.getScreens().first().visualBounds

        togglebutton("Live", selectFirst = false) {
            selectedProperty().addListener(liveChangeListener)
            disableWhen(displayVersesModel.group.booleanBinding{ list ->
                list?.isEmpty() ?: true
            })
        }

        projection.openWindow(StageStyle.TRANSPARENT, escapeClosesWindow = false)?.apply {
            this.hide()
        }

        vboxConstraints { vGrow = Priority.ALWAYS }
        paddingAll = 5
    }
}