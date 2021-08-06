package com.verseviewer.application.model.event

import javafx.scene.Node
import tornadofx.FXEvent
import tornadofx.Scope

class ChangeScene(val node : Node, val nodeScope : Scope? = null) : FXEvent()