package com.verseviewer.application.model.event

import com.verseviewer.application.model.Passage
import tornadofx.*

class RefreshList (val passages: List<Passage>) : FXEvent()