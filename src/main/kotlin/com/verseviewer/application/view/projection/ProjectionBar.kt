package com.verseviewer.application.view.projection

import com.verseviewer.application.model.ProjectionModel
import com.verseviewer.application.view.projection.Projection
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import javafx.stage.StageStyle
import tornadofx.*

class ProjectionBar : Fragment() {

    private val projectionModel : ProjectionModel by inject()
    private val projection =  find<Projection>()
    private var ft = 40.0

    private val liveChangeListener = ChangeListener<Boolean> { _, _, new ->
        if (new) {
            projection.openWindow(StageStyle.TRANSPARENT, escapeClosesWindow = false)
        }
        else {
            projection.close()
        }
    }

    private val list = listOf(1,2).asObservable()

    override val root = hbox {
        togglebutton("Black", selectFirst = false) {
        }
        togglebutton("Clear", selectFirst = false) {
            projectionModel.isVisible.bind(selectedProperty())
        }
        togglebutton("Live", selectFirst = false) {
            selectedProperty().addListener(liveChangeListener)
        }
        button("-") {
           action {
               ft -= 1.0
               projectionModel.font.value = Font.font(ft)
           }
        }
        button("+") {
            action {
                ft += 1.0
                projectionModel.font.value = Font.font(ft)
            }
        }
        combobox(property = projectionModel.displayIndex, values = list) {

        }
        vboxConstraints { vGrow = Priority.ALWAYS }
        paddingAll = 5
    }

    override fun onUndock() {
        super.onUndock()
//        projection.close()
    }

    override fun onDelete() {
        super.onDelete()
        projection.close()
    }
}