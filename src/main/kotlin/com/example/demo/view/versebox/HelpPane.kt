package com.example.demo.view.versebox

import com.example.demo.controller.DBController
import com.example.demo.model.Translation
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.FXCollections
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import tornadofx.*

class HelpPane : View("HelpPane") {
    private val dbController : DBController by inject()
    private val translations = FXCollections.observableArrayList(dbController.getTranslations())

    private val tex = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed ut convallis sapien. Mauris aliquet efficitur risus, eget maximus nunc ornare eu. Ut ut elit tincidunt, ultrices nunc eget, mollis dolor. Curabitur ut magna mi. Duis mi elit, ullamcorper eget erat id, tincidunt porta eros. Nam laoreet neque interdum tortor feugiat aliquam. Mauris sagittis sapien neque, finibus facilisis mi consequat in. Curabitur at lectus vitae libero sodales ullamcorper a sit amet metus. Aenean imperdiet ligula at sem pellentesque placerat. Aliquam mattis porttitor felis, id rhoncus odio convallis vel. Sed maximus lorem ultrices metus euismod, vitae accumsan nunc vehicula."

    override val root = drawer(multiselect = false) {
        val usage = item("Usage") {
            label(tex).isWrapText = true
        }


        val translations = item("Translations") {
            tableview(translations) {
                readonlyColumn("Abb", Translation::abbreviation)
                readonlyColumn("Name", Translation::name).enableTextWrap().remainingWidth()
                readonlyColumn("Lang", Translation::lang)
                columnResizePolicy = SmartResize.POLICY
            }
            expanded = true
        }

        usage.prefHeightProperty().bind(translations.heightProperty())
        items.addAll(usage, translations)
        useMaxSize = true

    }
}
