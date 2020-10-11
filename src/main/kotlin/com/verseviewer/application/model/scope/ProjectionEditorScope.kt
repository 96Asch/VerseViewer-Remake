package com.verseviewer.application.model.scope

import com.verseviewer.application.model.PreferenceModel
import com.verseviewer.application.model.ProjectionModel
import tornadofx.*

class ProjectionEditorScope : Scope() {
    val savedProjectionModel = ProjectionModel()
    val savedPreferenceModel = PreferenceModel()
}
