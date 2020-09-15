package com.verseviewer.application.model.event

import com.verseviewer.application.model.Verse
import tornadofx.FXEvent

class BroadcastVerses(val verses : List<Verse>) :  FXEvent()