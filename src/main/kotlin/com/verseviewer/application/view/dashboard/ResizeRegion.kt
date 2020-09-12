package com.verseviewer.application.view.dashboard

import javafx.scene.Node
import javafx.scene.shape.ClosePath
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.QuadCurveTo

open class ResizeRegion(icon : Node) : TileRegion(icon=icon) {

    val offset = 0.23809524

    override fun drawPath() {
        path.elements.clear()
        if (roundedCorner) {
            path.elements.add(MoveTo(size, size - size * offset))
            path.elements.add(LineTo(size, 0.0))
            path.elements.add(LineTo(0.0, size))
            path.elements.add(LineTo(size - size * offset, size))
            path.elements.add(QuadCurveTo(size, size, size, size - size * offset))
            path.elements.add(ClosePath())
        } else {
            path.elements.add(MoveTo(size, size))
            path.elements.add(LineTo(size, 0.0))
            path.elements.add(LineTo(0.0, size))
            path.elements.add(ClosePath())
        }

    }

    override fun drawIcon() {
        icon.relocate((size*offset*1.75), (size*offset*1.75))
    }


}