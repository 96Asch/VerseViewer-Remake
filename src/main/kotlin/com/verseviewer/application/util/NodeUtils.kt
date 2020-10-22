package com.verseviewer.application.util

import com.verseviewer.application.app.Styles
import com.verseviewer.application.model.event.NotificationType
import javafx.animation.PauseTransition
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration
import org.controlsfx.control.NotificationPane
import org.controlsfx.glyphfont.FontAwesome
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

fun NotificationPane.showForSeconds(type : NotificationType, message: String, duration: Int) {
    isCloseButtonVisible = false

    when (type) {
        NotificationType.NOTIFICATION -> {}
        NotificationType.WARNING -> graphic = Styles.fontAwesome.create(FontAwesome.Glyph.WARNING)
        NotificationType.ERROR -> graphic = Styles.fontAwesome.create(FontAwesome.Glyph.EXCLAMATION_TRIANGLE)
    }

    if (graphic != null)
        show(message, graphic)
    else
        show(message)

    val pause = PauseTransition(Duration.seconds(duration.toDouble()))
    pause.onFinished = EventHandler { hide() }
    pause.play()
}