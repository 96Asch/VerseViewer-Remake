package com.verseviewer.application.model.event

import tornadofx.*

class PlayFrameAnimation(scope : Scope) : FXEvent(EventBus.RunOn.ApplicationThread, scope)
class PlayReverseFrameAnimation(scope : Scope) : FXEvent(EventBus.RunOn.ApplicationThread, scope)
