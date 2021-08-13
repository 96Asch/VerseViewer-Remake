package com.verseviewer.application.controller

import com.verseviewer.application.model.Snapshot
import com.verseviewer.application.model.event.NotificationType
import com.verseviewer.application.model.event.SendGlobalNotification
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.stage.FileChooser
import tornadofx.*
import java.sql.SQLException

class MainViewController : Controller() {

    val databasePathProperty = SimpleStringProperty("")
    var databasePath: String by databasePathProperty

    private val snapshotIndexProperty = SimpleIntegerProperty()
    var snapshotIndex: Int by snapshotIndexProperty

    val snapshotLoadedProperty = SimpleBooleanProperty(false)
    var snapshotLoaded by snapshotLoadedProperty
    
    private val dbController : DBController by inject()

    val exts = listOf(FileChooser.ExtensionFilter("sqlite database (.db)", "*.db"))
    val prefName = "VerseViewer"

    fun setDBPath(path : String) {
        databasePath = path
        dbController.initDB(path)
    }

    fun loadSnapshot(id : Int): Snapshot? {
        var snap: Snapshot? = null
        try {
            snap = dbController.getSnapshot(id)
            snapshotLoaded = true
        }
        catch (e : SQLException) {
            val message = "${e.localizedMessage}"
            fire(SendGlobalNotification(message, NotificationType.ERROR, 5))
        }
        return snap
    }
}