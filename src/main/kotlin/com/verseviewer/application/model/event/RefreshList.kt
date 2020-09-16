package com.verseviewer.application.model.event

import com.verseviewer.application.model.Passage
import com.verseviewer.application.model.datastructure.GroupType
import tornadofx.*

class RefreshList (val passages: List<Passage>, val type: GroupType) : FXEvent()