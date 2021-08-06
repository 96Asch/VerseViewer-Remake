package com.verseviewer.application.model.scope

import com.verseviewer.application.model.SnapshotModel
import com.verseviewer.application.model.ProjectionModel
import tornadofx.*

class ProjectionEditorScope : Scope() {
    val savedProjectionModel = ProjectionModel()
    val savedSnapshotModel = SnapshotModel()
}
