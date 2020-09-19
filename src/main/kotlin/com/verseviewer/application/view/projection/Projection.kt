package com.verseviewer.application.view.projection

import com.verseviewer.application.controller.ProjectionController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.ProjectionModel
import javafx.scene.text.Font
import javafx.stage.Screen
import tornadofx.*

class Projection : Fragment() {

    private val displayVersesModel : DisplayVersesModel by inject()
    private val controller : ProjectionController by inject();
    private val projectionModel : ProjectionModel by inject()

    override val root = anchorpane {
        add(find<PassageBox>())
    }

    override fun onDock() {
        super.onDock()
        modalStage?.isMaximized = true
        modalStage?.isResizable = false
//        modalStage?.opacity = 0.5
        if (projectionModel.displayIndex.value >= 0 && projectionModel.displayIndex.value < Screen.getScreens().size) {
            val bounds = Screen.getScreens()[projectionModel.displayIndex.value].visualBounds
            modalStage?.x = bounds.minX
            modalStage?.y = bounds.minY
        }
    }
}