package com.example.demo.view.dashboard

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
//        icon.elements.clear()
//        icon.elements.add(MoveTo(size * 0.185714285714286, size * 0.119047619047619))
//        icon.elements.add(LineTo(size * 0.254761904761905, size * 0.185714285714286))
//        icon.elements.add(LineTo(size * 0.185714285714286, size * 0.254761904761905))
//        icon.elements.add(LineTo(size * 0.119047619047619, size * 0.185714285714286))
//        icon.elements.add(ClosePath())
//        icon.elements.add(MoveTo(size * 0.304761904761905, size * 0.238095238095238))
//        icon.elements.add(LineTo(size * 0.466666666666667, size * 0.4))
//        icon.elements.add(LineTo(size * 0.4, size * 0.466666666666667))
//        icon.elements.add(LineTo(size * 0.238095238095238, size * 0.304761904761905))
//        icon.elements.add(ClosePath())
        icon.relocate(size * offset/4, size * offset/4)
    }
}