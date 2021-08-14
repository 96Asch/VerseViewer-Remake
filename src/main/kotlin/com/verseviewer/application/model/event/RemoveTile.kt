package com.verseviewer.application.model.event

import eu.hansolo.tilesfx.Tile
import tornadofx.FXEvent

class RemoveTile(val tile : Tile): FXEvent()