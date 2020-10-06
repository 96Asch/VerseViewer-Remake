package com.verseviewer.application.model.scope

import com.verseviewer.application.model.FontModel
import com.verseviewer.application.model.ProjectionModel
import tornadofx.*

class ProjectionEditorScope : Scope() {
    val savedFontModel = FontModel()
    val savedProjectionModel = ProjectionModel()
}
