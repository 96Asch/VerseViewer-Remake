package com.verseviewer.application.model.event

import com.verseviewer.application.model.Passage
import tornadofx.FXEvent

class BroadcastVerses(val passages : List<Passage>) :  FXEvent()