package com.verseviewer.application.model.event

import javafx.scene.Node
import tornadofx.*

enum class NotificationType {
    WARNING,
    ERROR,
    NOTIFICATION
}

class SendVBNotification(val message: String, val type: NotificationType, val duration: Int) : FXEvent()
class SendScheduleNotification(val message: String, val type: NotificationType, val duration: Int) : FXEvent()
class SendDBNotification(val message: String) : FXEvent()