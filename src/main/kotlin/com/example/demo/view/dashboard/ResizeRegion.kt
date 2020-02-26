package com.example.demo.view.dashboard

import javafx.scene.shape.ClosePath
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.QuadCurveTo

class ResizeRegion : TileRegion() {
    override fun drawPath() {
        path.elements.clear()
        if (roundedCorner) {
            path.elements.add(MoveTo(size, size - size * 0.23809524))
            path.elements.add(LineTo(size, 0.0))
            path.elements.add(LineTo(0.0, size))
            path.elements.add(LineTo(size - size * 0.23809524, size))
            path.elements.add(QuadCurveTo(size, size, size, size - size * 0.23809524))
            path.elements.add(ClosePath())
        } else {
            path.elements.add(MoveTo(size, size))
            path.elements.add(LineTo(size, 0.0))
            path.elements.add(LineTo(0.0, size))
            path.elements.add(ClosePath())
        }

    }

    override fun drawIcon() {
        val offset = 0.15
        icon.elements.clear()
        icon.elements.add(MoveTo(size * (1 - offset), size * (1 - offset)))
        icon.elements.add(LineTo(size * (1 - offset), size * offset * 2))
        icon.elements.add(LineTo(size * offset * 2, size * (1 - offset)))
        icon.elements.add(ClosePath())
    }


}