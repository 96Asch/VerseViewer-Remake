package com.verseviewer.application.view.projection

import com.verseviewer.application.app.Styles
import javafx.animation.*
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Rectangle2D
import javafx.scene.CacheHint
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.layout.Region
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import tornadofx.*


class Frame(val width : Double, val height : Double) : Group() {

    private val frameSegmentTime = 400.0
    private val fadeTime = 2000.0
    private val fadeDelayTime = 800.0
    private var isReverse = false
    private var frameAnimation: Animation? = null
    private val animation = ParallelTransition()
    private val list = mutableListOf<Node>()

    fun buildSimpleSequentialAnimation(margin : Double, topMargin : Double, topLineFactor : Double) {
        children.clear()
        isCache = true
        cacheHint = CacheHint.SPEED

        val tloffset = 20.0
        val xRight = width - margin
        val bottomY = height - margin

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
        sequentialAnimation.children.add(buildLineAnimation(topLine, (xRight - margin) / topLineFactor, topMargin))

        frameAnimation = sequentialAnimation

        children.addAll(topLeftLine, leftLine, bottomLine, rightLine, topLine)
        children.filterIsInstance<Line>().forEach {
            it.addClass(Styles.frame)
            it.isVisible = false
        }
        list.addAll(children.toList())
    }

    fun initAnimation(header : Node, body : Node) {
        animation.children.clear()

        val headerFadeAnimation = buildFadeTransition(header)
        val bodyFadeAnimation = buildFadeTransition(body).apply {
            onFinished = EventHandler {
                if (!isReverse) {
                    println("Paused")
                    println(animation.children.size)
                    animation.pause()
                }
            }
        }

        animation.children.addAll(frameAnimation, headerFadeAnimation, bodyFadeAnimation)
        animation.cycleCount = 2
        animation.isAutoReverse = true
    }

    fun playFromStart(rate : Double = 1.0) {
        isReverse = false
        children.forEach { it.isVisible = false }
        animation.rate = rate
        println("From Start")
        animation.playFromStart()
    }

    fun reversePlay() {
        if (!isReverse && animation.status == Animation.Status.PAUSED) {
            isReverse = true
            println("Reverse")
            animation.play()
        }
    }

    private fun buildFadeTransition(header: Node) = FadeTransition(Duration.millis(fadeTime), header).apply {
        fromValue = 0.0
        toValue = 1.0
        delay = Duration.millis(fadeDelayTime)
    }

    private fun buildLineAnimation(line: Line, endX: Double, endY: Double, segmentTime : Double = frameSegmentTime): Animation {
        return Timeline(
                KeyFrame(Duration.millis(1.0),
                        KeyValue(line.visibleProperty(), true)),  // show

                // Build a growing animation for a line
                KeyFrame(Duration.millis(segmentTime),
                        KeyValue(line.endXProperty(), endX),
                        KeyValue(line.endYProperty(), endY)
                )
        )
    }
}

fun EventTarget.frame(width : Double, height : Double, op: Frame.() -> Unit = {}): Frame {
    val frame = Frame(width, height)
    return opcr(this, frame, op)
}