package com.verseviewer.application.view.dashboard

import javafx.scene.text.Text

class CounterRegion(val counterText: Text = Text()) : ResizeRegion(counterText) {

    override fun drawIcon() {
        icon.relocate((size*offset*1.9), (size*offset))
    }
}