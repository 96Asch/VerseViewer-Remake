package com.verseviewer.application.view.projection

import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.ProjectionModel
import javafx.scene.text.Font
import javafx.stage.Screen
import tornadofx.*

class Projection : Fragment() {

    private val displayVersesModel : DisplayVersesModel by inject()
    private val projectionModel : ProjectionModel by inject()

    override val root = anchorpane {

        text {
            textProperty().bind(displayVersesModel.header)
//            val bounds = Screen.getScreens()[projectionModel.displayIndex.value].bounds
//            x = bounds.width / 2
//            y = bounds.height / 2 - 200
            font = Font.font(30.0)
        }
        println("projection: $displayVersesModel")
        text {
            textProperty().bind(displayVersesModel.bodies)
            wrappingWidth = 500.0
            val bounds = Screen.getScreens()[projectionModel.displayIndex.value].bounds
//            x = bounds.width / 2
            y = bounds.height / 2
            font = Font.font(30.0)
        }


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