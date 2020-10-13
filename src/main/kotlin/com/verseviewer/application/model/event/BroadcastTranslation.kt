package com.verseviewer.application.model.event

import com.verseviewer.application.model.Translation
import tornadofx.FXEvent

class BroadcastTranslation(val translation : Translation) : FXEvent()