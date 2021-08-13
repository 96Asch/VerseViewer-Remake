package com.verseviewer.application.model.event

import com.verseviewer.application.model.datastructure.Dimension
import tornadofx.FXEvent

class HighlightCells(val allowed : Boolean, val dimension : Dimension) : FXEvent()