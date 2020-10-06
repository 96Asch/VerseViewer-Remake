package com.verseviewer.application.view.main

import com.verseviewer.application.model.FontData
import com.verseviewer.application.model.FontModel
import com.verseviewer.application.model.ProjectionModel
import com.verseviewer.application.model.event.SaveProjectionEditorSettings
import com.verseviewer.application.model.scope.ProjectionEditorScope
import com.verseviewer.application.view.dashboard.DashBoard
import com.verseviewer.application.view.dashboard.DashBoardEditor
import com.verseviewer.application.view.editor.ProjectionEditor
import javafx.geometry.Orientation
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.Parent
import tornadofx.*

class MainView : View() {

    private val fontModel : FontModel by inject()
    private val projectionModel : ProjectionModel by inject()

    private val dashboardView : DashBoard by inject()

    private val projectionEditorScope = ProjectionEditorScope()
    private val projectionEditorView : ProjectionEditor by inject()

    private val dashBoardEditorView : DashBoardEditor by inject(Scope())

    override val root = borderpane {

        top = listmenu {
            item("Dashboard") {
                activeItem = this
                whenSelected {  center.replaceWith(dashboardView.root)  }
            }
            item("Projection Settings") {
                whenSelected { setupProjectionEditorView(center) }
            }
            item("Dashboard Editor") {
                whenSelected { center.replaceWith(dashBoardEditorView.root) }
            }

            orientation = Orientation.HORIZONTAL
            iconPosition = Side.TOP
        }

        center = find<DashBoard>().root
    }

    private fun setupProjectionEditorView(node : Node) {
        projectionEditorScope.savedFontModel.item = fontModel.item
        projectionEditorScope.savedProjectionModel.item = projectionModel.item
        node.replaceWith(find(ProjectionEditor::class, projectionEditorScope).root)
    }

    init {
        fontModel.item = FontData(50)
        subscribe<SaveProjectionEditorSettings> {
            println("Saved settings")
            fontModel.item = projectionEditorScope.savedFontModel.item
            projectionModel.item = projectionEditorScope.savedProjectionModel.item
        }
    }
}