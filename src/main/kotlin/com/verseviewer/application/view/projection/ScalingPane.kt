package com.verseviewer.application.view.projection

import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.transform.Scale
import tornadofx.onChange

class ScalingPane(child : Node, screenWidth : Double, screenHeight : Double) : Pane(child) {

    private val scaleXProperty = SimpleDoubleProperty(0.0)
    private val scaleYProperty = SimpleDoubleProperty(0.0)

    init {
        child.layoutX = 0.0
        child.layoutY = 0.0

        scaleXProperty.bind(widthProperty().divide(screenWidth))
        scaleYProperty.bind(heightProperty().divide(screenHeight))

        transforms.add(Scale(scaleXProperty.value, scaleYProperty.value, 0.0, 0.0))

        scaleXProperty.onChange {
            transforms.clear()
            transforms.add(Scale(it, scaleYProperty.value, 0.0, 0.0))
        }

        scaleYProperty.onChange {
            transforms.clear()
            transforms.add(Scale(scaleXProperty.value, it, 0.0, 0.0))
        }
    }


}