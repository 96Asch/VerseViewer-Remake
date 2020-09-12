package com.verseviewer.application.model.event

import com.verseviewer.application.model.Verse
import com.verseviewer.application.model.datastructure.GroupType
import tornadofx.*

class RefreshList (val verses: List<Verse>, val type: GroupType) : FXEvent()