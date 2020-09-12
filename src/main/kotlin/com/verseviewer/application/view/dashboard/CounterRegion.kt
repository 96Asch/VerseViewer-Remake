package com.verseviewer.application.view.dashboard

import javafx.scene.control.Label

class CounterRegion(val counterLabel: Label = Label()) : ResizeRegion(counterLabel) {

    override fun drawIcon() {
        icon.relocate((size*offset*2), (size*offset*1.75))
    }
}