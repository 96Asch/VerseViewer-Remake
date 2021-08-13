package com.verseviewer.application.view.projection

import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Pagination
import javafx.scene.layout.Pane
import javafx.scene.transform.Scale
import tornadofx.onChange
import tornadofx.opcr

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

fun EventTarget.scalingpane(child : Node, screenWidth : Double, screenHeight : Double, op: ScalingPane.() -> Unit = {}): ScalingPane {
    val scalingPane = ScalingPane(child, screenWidth, screenHeight)
    return opcr(this, scalingPane, op)
}