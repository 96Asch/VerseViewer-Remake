package com.verseviewer.application.model.event

import javafx.scene.Node
import tornadofx.*

enum class NotificationType {
    WARNING,
    ERROR,
    NOTIFICATION
}

class SendNotification(val message: String, val type: NotificationType, val duration: Int) : FXEvent()