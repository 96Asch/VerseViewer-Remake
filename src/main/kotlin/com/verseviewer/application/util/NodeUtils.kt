package com.verseviewer.application.util

import javafx.animation.PauseTransition
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration
import org.controlsfx.control.NotificationPane
import kotlin.math.ceil

class NodeUtils {

    companion object {
        private var helper: Text = Text()

        fun computeTextWidth(font: Font?, text: String?, wrappingWidth: Double): Double {
            helper.text = text
            helper.font = font
            // Note that the wrapping width needs to be set to zero before
            // getting the text's real preferred width.
            helper.wrappingWidth = 0.0
            val w = helper.prefWidth(-1.0).coerceAtMost(wrappingWidth)
            helper.wrappingWidth = ceil(w)
            return ceil(helper.layoutBounds.width)
        }

        fun computeTextHeight(font: Font?, text: String?, wrappingWidth: Double): Double {
            helper.text = text
            helper.font = font
            helper.wrappingWidth = wrappingWidth
            return helper.layoutBounds.height
        }
    }
}

fun NotificationPane.showForSeconds(message: String, graphic: Node? = null, duration: Int) {
    if (graphic != null)
        show(message, graphic)
    else
        show(message)

    val pause = PauseTransition(Duration.seconds(duration.toDouble()))
    pause.onFinished = EventHandler { hide() }
    pause.play()
}