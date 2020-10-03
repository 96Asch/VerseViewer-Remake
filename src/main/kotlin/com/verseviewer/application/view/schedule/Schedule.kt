package com.verseviewer.application.view.schedule

import com.verseviewer.application.app.Styles
import tornadofx.*
import com.verseviewer.application.controller.DragVerseController
import com.verseviewer.application.model.DisplayVersesModel
import com.verseviewer.application.model.datastructure.VerseGroup
import com.verseviewer.application.model.scope.ScheduleScope
import javafx.beans.Observable
import javafx.event.ActionEvent
import javafx.geometry.Side
import javafx.scene.control.ScrollPane
import javafx.scene.control.TableView
import javafx.scene.input.DragEvent
import javafx.scene.layout.Priority
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import tornadofx.controlsfx.detail
import tornadofx.controlsfx.hiddensidepane
import tornadofx.controlsfx.masterdetailpane

class Schedule : Fragment("My View") {
    override val scope = super.scope as ScheduleScope

    private val controller = scope.controller
    private val dragVerseController : DragVerseController by inject(FX.defaultScope)
    private val displayModel : DisplayVersesModel by inject(FX.defaultScope)

    private var tv : TableView<VerseGroup> by singleAssign()

    override val root = hiddensidepane {
        content = masterdetailpane {
            tv = tableview (controller.list) {
                readonlyColumn("Verse", VerseGroup::verses) {
                    isSortable = false
                    cellFormat {
                        graphic = text {
                            text = it.joinToString { "${it.translation.abbreviation} ${it.book} ${it.chapter}:${it.verse}" }
                                wrappingWidthProperty().bind(widthProperty().subtract(20))
                            }
                        }
                        remainingWidth()
                    }
                    bindSelected(displayModel)

                    selectionModel.selectedItemProperty().addListener{_, _, new ->
                        new?.let { controller.setDetail(new) }
                    }

                    setOnDragDropped(::dragDrop)
                    setOnDragOver(::dragOver)
                    multiSelect(true)
                    smartResize()
                    hboxConstraints {
                        hgrow = Priority.ALWAYS
                    }
                }

                masterNode = tv

                detail {
                    listview(controller.detailList) {
                        cellFormat {
                            graphic = form {
                               fieldset("${it.translation.abbreviation} ${it.book} ${it.chapter} : ${it.verse}") {
                                    text(it.text) {
                                        wrappingWidthProperty().bind(this@listview.widthProperty().subtract(50))
                                    }
                                }
                            }
                            isMouseTransparent = true
                            isFocusTraversable = false
                        }
                    }
                }
                dividerPosition = 0.4
                showDetailNodeProperty().bind(controller.detailList.sizeProperty.greaterThan(0).and(this@hiddensidepane.hoverProperty()))
                detailSide = Side.BOTTOM

            }

        val glyph = GlyphFontRegistry.font("FontAwesome")
            right = scrollpane {
                this.addClass(Styles.transparent)
                vbox {
                    this.addClass(Styles.transparent)
                    paddingAll = 10
                    paddingTop = 30
                    button(graphic = glyph.create(FontAwesome.Glyph.SAVE))
                    button(graphic = glyph.create(FontAwesome.Glyph.FOLDER_OPEN))
                    button(graphic = glyph.create(FontAwesome.Glyph.ARROW_UP)).setOnAction(::moveSelectedUp)
                    button(graphic = glyph.create(FontAwesome.Glyph.ARROW_DOWN)).setOnAction(::moveSelectedDown)
                    button(graphic = glyph.create(FontAwesome.Glyph.LINK)).setOnAction(::groupSelected)
                    button(graphic = glyph.create(FontAwesome.Glyph.UNLINK)).setOnAction(::ungroupSelected)
                    button(graphic = glyph.create(FontAwesome.Glyph.REMOVE)).setOnAction(::deleteSelected)
                    button(graphic = glyph.create(FontAwesome.Glyph.TRASH)).setOnAction(::clearSchedule)
                }
                vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            }
            triggerDistance = 80.toDouble()

    }

    private fun moveSelectedUp(evt : ActionEvent) {
        controller.moveSelectedUp(tv)
        tv.requestFocus()
        evt.consume()
    }

    private fun moveSelectedDown(evt : ActionEvent) {
        controller.moveSelectedDown(tv)
        tv.requestFocus()
        evt.consume()
    }

    private fun groupSelected(evt : ActionEvent) {
        controller.groupSelected(tv)
        tv.requestFocus()
        evt.consume()
    }

    private fun ungroupSelected(evt : ActionEvent) {
        controller.ungroupSelected(tv)
        tv.requestFocus()
        evt.consume()
    }

    private fun deleteSelected(evt : ActionEvent) {
        controller.deleteSelected(tv)
        tv.requestFocus()
        evt.consume()
    }

    private fun clearSchedule(evt : ActionEvent) {
        controller.clear()
        tv.requestFocus()
        evt.consume()
    }

    private fun dragOver(evt: DragEvent) {
        dragVerseController.dragOver(evt, tv)
    }

    private fun dragDrop(evt: DragEvent) {
        dragVerseController.dragDrop(evt, controller.list)
    }
 }
