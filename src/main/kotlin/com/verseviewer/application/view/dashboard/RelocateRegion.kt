package com.verseviewer.application.view.dashboard

import javafx.scene.Node
import javafx.scene.shape.ClosePath
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.QuadCurveTo

class RelocateRegion(icon : Node) : TileRegion(icon=icon) {

    private val offset = 0.23809524

    override fun drawPath() {
        path.elements.clear()
        if (roundedCorner) {
            path.elements.add(MoveTo(size * offset, 0.0))
            path.elements.add(LineTo(size, 0.0))
            path.elements.add(LineTo(0.0, size))
            path.elements.add(LineTo(0.0, size * offset))
            path.elements.add(QuadCurveTo(0.0, 0.0, size * offset, 0.0))
            path.elements.add(ClosePath())
        } else {
            path.elements.add(MoveTo(0.0, 0.0))
            path.elements.add(LineTo(size, 0.0))
            path.elements.add(LineTo(0.0, size))
            path.elements.add(ClosePath())
        }
    }

    override fun drawIcon() {
        icon.relocate(size * offset/4, size * offset/4)
    }
}