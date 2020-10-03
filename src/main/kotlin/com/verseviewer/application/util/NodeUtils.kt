package com.verseviewer.application.util

import javafx.scene.text.Font
import javafx.scene.text.Text
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