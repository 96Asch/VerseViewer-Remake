package com.verseviewer.application.view.projection

import com.verseviewer.application.app.Styles
import javafx.animation.*
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Bounds
import javafx.geometry.Rectangle2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.layout.Region
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import tornadofx.*


class Frame(parentBoundsProperty: ReadOnlyObjectProperty<Bounds>) : Group() {

    private val frameSegmentTime = 400.0
    private val fadeTime = 2000.0
    private val fadeDelayTime = 800.0
    private val bounds: Bounds by parentBoundsProperty
    private var isReverse = false
    private var frameAnimation: Animation? = null
    private var headerAnimation: Animation? = null
    private var bodyAnimation: Animation? = null
    private val animation = ParallelTransition()
    private val list = mutableListOf<Node>()


    fun buildSimpleSequentialAnimation(margin : Double, topMargin : Double) {
        children.clear()

        val tloffset = 20.0
        val xRight = bounds.width - margin
        val bottomY = bounds.height - margin

        val topLeftLine = Line(margin + tloffset, topMargin, margin + tloffset, topMargin)
        val leftLine = Line(margin, topMargin, margin, topMargin)
        val bottomLine = Line(margin, bottomY, margin, bottomY)
        val rightLine = Line(xRight, bottomY, xRight, bottomY)
        val topLine = Line(xRight, topMargin, xRight, topMargin)

        val sequentialAnimation = SequentialTransition()

        sequentialAnimation.children.add(buildLineAnimation(topLeftLine, margin, topMargin, frameSegmentTime/2))
        sequentialAnimation.children.add(buildLineAnimation(leftLine, margin, bottomY))
        sequentialAnimation.children.add(buildLineAnimation(bottomLine, xRight, bottomY))
        sequentialAnimation.children.add(buildLineAnimation(rightLine, xRight, topMargin))
        sequentialAnimation.children.add(buildLineAnimation(topLine, (xRight - margin) / 1.5, topMargin))

        frameAnimation = sequentialAnimation

        children.addAll(topLeftLine, leftLine, bottomLine, rightLine, topLine)
        children.filterIsInstance<Line>().forEach {
            it.addClass(Styles.frame)
            it.isVisible = false
        }
        list.addAll(children.toList())
    }

    fun initAnimation() {
        animation.children.clear()

        animation.children.addAll(frameAnimation, headerAnimation, bodyAnimation)
        animation.cycleCount = 2
        animation.isAutoReverse = true
    }

    fun playFromStart(rate : Double = 1.0) {
        isReverse = false
        children.forEach { it.isVisible = false }
        animation.rate = rate
        animation.playFromStart()
        
    }

    fun reversePlay() {
        isReverse = true
        animation.play()
    }

    fun buildHeaderFadeTransition(header: Node) {
        headerAnimation = FadeTransition(Duration.millis(fadeTime), header).apply {
            fromValue = 0.0
            toValue = 1.0
            delay = Duration.millis(fadeDelayTime)
        }
    }

    fun buildBodyFadeTransition(body : Node) {
        bodyAnimation = FadeTransition(Duration.millis(fadeTime), body).apply {
            fromValue = 0.0
            toValue = 1.0
            delay = Duration.millis(fadeDelayTime)
            onFinished = EventHandler {
                if (!isReverse)
                    animation.pause()
            }
        }
    }

    private fun buildRectangleHorizontalAnimation(rect : Rectangle, endWidth : Double): Animation {
        return Timeline(
                KeyFrame(Duration.millis(1.0),
                        KeyValue(rect.visibleProperty(), true)),
                KeyFrame(Duration.millis(frameSegmentTime),
                        KeyValue(rect.widthProperty(), endWidth))
        )
    }

    private fun buildLineAnimation(line: Line, endX: Double, endY: Double, segmentTime : Double = frameSegmentTime): Animation {
        return Timeline(
                KeyFrame(Duration.millis(1.0),
                        KeyValue(line.visibleProperty(), true)),  // show
                KeyFrame(Duration.millis(segmentTime),
                        KeyValue(line.endXProperty(), endX),
                        KeyValue(line.endYProperty(), endY)
                )
        )
    }

    private fun getBoundsUpfront(node: Region): Rectangle2D {
        // Calculate main title width and height
        val titleRoot = Group()
        Scene(titleRoot)
        titleRoot.children.add(node)
        titleRoot.applyCss()
        titleRoot.layout()
        return Rectangle2D(0.0, 0.0, node.width, node.height)
    }
}