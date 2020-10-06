package com.verseviewer.application.model.event

import tornadofx.*

class OpenProjection(scope : Scope) : FXEvent(EventBus.RunOn.ApplicationThread, scope)
class CloseProjection(scope : Scope) : FXEvent(EventBus.RunOn.ApplicationThread, scope)