package com.example.demo.model.event

import com.example.demo.model.Verse
import com.example.demo.model.datastructure.GroupType
import tornadofx.*

class RefreshList (val verses: List<Verse>, val type: GroupType) : FXEvent()