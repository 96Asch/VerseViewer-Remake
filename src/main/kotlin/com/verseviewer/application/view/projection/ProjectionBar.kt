package com.verseviewer.application.view.projection

import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.ProjectionModel
import com.verseviewer.application.model.event.CloseProjection
import com.verseviewer.application.model.event.OpenProjection
import javafx.scene.layout.Priority
import javafx.stage.Screen
import javafx.stage.StageStyle
import tornadofx.*

class ProjectionBar : Fragment() {

    private val projectionModel : ProjectionModel by inject()
    private val projection = find<Projection>(mapOf("isCloseable" to true))
    private val displayVersesModel : DisplayVersesModel by inject()

    private val liveChangeListener = ChangeListener<Boolean> { _, _, new ->
        if (new) {
            projection.openWindow(StageStyle.TRANSPARENT, escapeClosesWindow = false)
            fire(OpenProjection())
        }
        else {
            fire(CloseProjection())
        }
        projectionModel.isLive = new
    }

    private val list = Screen.getScreens().mapIndexed { index, _ -> index }.asObservable()

    override val root = hbox {
        togglebutton("Live", selectFirst = false) {
            selectedProperty().addListener(liveChangeListener)
            disableWhen(displayVersesModel.group.booleanBinding{ list ->
                list?.isEmpty() ?: true
            })
        }
        combobox(property = projectionModel.displayIndexProperty, values = list) {
            selectionModel.selectedItemProperty().onChange {
                if (it != null) {
                    projectionModel.displayIndex = it
                    projectionModel.screenBounds = Screen.getScreens()[it.toInt()].visualBounds
                }
            }
        }

        projection.openWindow(StageStyle.TRANSPARENT, escapeClosesWindow = false)?.apply {
            this.hide()
        }

        vboxConstraints { vGrow = Priority.ALWAYS }
        paddingAll = 5
    }

}