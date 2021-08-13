package com.verseviewer.application.model.event

import com.verseviewer.application.model.TileProperties
import tornadofx.FXEvent

class PlaceTile(val tileProperty : TileProperties) : FXEvent()