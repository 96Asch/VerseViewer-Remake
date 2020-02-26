package com.example.demo.view.dashboard

import eu.hansolo.tilesfx.Tile
import javafx.beans.Observable
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.Path

abstract class TileRegion(protected var size: Double = 0.0,
                          private var w: Double = 0.0,
                          private var h: Double = 0.0,
                          protected val path: Path = Path(),
                          protected val icon: Path = Path(),
                          var backgroundColor: Color = Tile.GREEN,
                          var foregroundColor: Color = Tile.FOREGROUND,
                          var roundedCorner: Boolean = true) : Region() {

    // ******************** Initialization ************************************
    private fun initGraphics() {
        if (prefWidth.compareTo(0.0) <= 0 || prefHeight.compareTo(0.0) <= 0
                || width.compareTo(0.0) <= 0 || height.compareTo(0.0) <= 0) {
            if (prefWidth > 0 && prefHeight > 0) {
                setPrefSize(prefWidth, prefHeight)
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT)
            }
        }
        path.stroke = Color.TRANSPARENT
        icon.stroke = Color.TRANSPARENT
        icon.isMouseTransparent = true
        children.setAll(path, icon)
    }

    private fun registerListeners() {
        widthProperty().addListener { o: Observable? -> resize() }
        heightProperty().addListener { o: Observable? -> resize() }
    }

    // ******************** Methods *******************************************
    public override fun layoutChildren() {
        super.layoutChildren()
    }

    override fun computeMinWidth(HEIGHT: Double): Double {
        return MINIMUM_WIDTH
    }

    override fun computeMinHeight(WIDTH: Double): Double {
        return MINIMUM_HEIGHT
    }

    override fun computeMaxWidth(HEIGHT: Double): Double {
        return MAXIMUM_WIDTH
    }

    override fun computeMaxHeight(WIDTH: Double): Double {
        return MAXIMUM_HEIGHT
    }

    public override fun getChildren(): ObservableList<Node> {
        return super.getChildren()
    }

    // ******************** Resizing ******************************************
    private fun resize() {
        w = width - insets.left - insets.right
        h = height - insets.top - insets.bottom
        size = if (w < h) w else h
        if (w > 0 && h > 0) {
            drawPath()
            drawIcon()
            redraw()
        }
    }

    abstract fun drawPath()

    abstract fun drawIcon()

    private fun redraw() {
        path.fill = backgroundColor
        icon.fill = foregroundColor
    }

    companion object {
        private const val PREFERRED_WIDTH = 52.0
        private const val PREFERRED_HEIGHT = 52.0
        private const val MINIMUM_WIDTH = 1.0
        private const val MINIMUM_HEIGHT = 1.0
        private const val MAXIMUM_WIDTH = 1024.0
        private const val MAXIMUM_HEIGHT = 1024.0
    }

    // ******************** Constructors **************************************
    init {
        initGraphics()
        registerListeners()
    }
}