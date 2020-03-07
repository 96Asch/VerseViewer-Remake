package com.example.demo.view.dashboard

import javafx.scene.Node
import javafx.scene.shape.ClosePath
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.QuadCurveTo


class CloseRegion(icon : Node) : TileRegion(icon=icon) {
    private val offset = 0.23809524
    
    override fun drawPath() {
        path.elements.clear()
        if (roundedCorner) {
            path.elements.add(MoveTo(0.0, 0.0))
            path.elements.add(LineTo(size - size * offset, 0.0))
            path.elements.add(QuadCurveTo(size, 0.0, size, size * offset))
            path.elements.add(LineTo(size, size))
            path.elements.add(LineTo(0.0, size))
            path.elements.add(ClosePath())
        } else {
            path.elements.add(MoveTo(0.0, 0.0))
            path.elements.add(LineTo(size, 0.0))
            path.elements.add(LineTo(size, size))
            path.elements.add(ClosePath())
        }
    }

    override fun drawIcon() {
        icon.relocate(size/4, size/4)
//        icon.elements.clear()
//        icon.elements.add(MoveTo(size * 0.802380952380952, size * 0.123809523809524))
//        icon.elements.add(LineTo(size * 0.871428571428571, size * 0.19047619047619))
//        icon.elements.add(LineTo(size * 0.70952380952381, size * 0.352380952380952))
//        icon.elements.add(LineTo(size * 0.642857142857143, size * 0.283333333333333))
//        icon.elements.add(ClosePath())
//        icon.elements.add(MoveTo(size * 0.588095238095238, size * 0.338095238095238))
//        icon.elements.add(LineTo(size * 0.657142857142857, size * 0.404761904761905))
//        icon.elements.add(LineTo(size * 0.588095238095238, size * 0.471428571428571))
//        icon.elements.add(LineTo(size * 0.521428571428571, size * 0.404761904761905))
//        icon.elements.add(ClosePath())
    }


}