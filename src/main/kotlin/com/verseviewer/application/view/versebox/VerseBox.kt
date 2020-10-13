package com.verseviewer.application.view.versebox

import com.verseviewer.application.app.Styles
import com.verseviewer.application.controller.DragVerseController
import com.verseviewer.application.controller.VerseBoxController
import com.verseviewer.application.controller.VerseSearchController
import com.verseviewer.application.model.*
import com.verseviewer.application.model.datastructure.VerseGroup
import com.verseviewer.application.model.event.*
import javafx.animation.PauseTransition
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.input.*
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import javafx.stage.StageStyle
import javafx.util.Duration
import org.controlsfx.control.NotificationPane
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.notificationPane


class VerseBox : Fragment() {
    private val passageModel : PassageModel by inject()
    private val displayModel : DisplayVersesModel by inject()

    private val controller : VerseBoxController by inject()
    private val dragVerseController : DragVerseController by inject()
    private val verseSearchController : VerseSearchController by inject()

    private val helpPane : HelpPane by inject()
    private val tv = tableview(controller.verseList)

    private val onSelectionChange : ListChangeListener<Passage> = ListChangeListener { changed ->
        if (changed.list.isEmpty().not() && !inGroupModeProperty.value && changed.list.size == 1) {
            displayModel.item =  VerseGroup(changed.list.toMutableList())
            fire(DeselectVerses(tv.toString()))
        }
    }

    private val multiCursor = Cursor.CROSSHAIR
    private var inGroupModeProperty = SimpleBooleanProperty(false)

    private val warningIm = imageview("icons/warning.png")
    private val errorIm = imageview("icons/error.png")

    override val root = vbox {
        notificationPane {
            content = tv.apply {
                readonlyColumn("B", Passage::book).isSortable = false
                readonlyColumn("C", Passage::chapter).isSortable = false
                readonlyColumn("V", Passage::verse).isSortable = false
                column("Text", Passage::textProperty).enableTextWrap().remainingWidth().isSortable = false

                onUserSelect {
                    openInternalWindow<VerseEditor>(escapeClosesWindow = true)
                }
                bindSelected(passageModel)
                multiSelect(true)

                multiSelectButtonEvent(tv)
                hoverProperty().and(inGroupModeProperty).addListener { _, _, new ->
                    if (new)
                        scene.cursor = multiCursor
                    else
                        scene.cursor = Cursor.DEFAULT
                }
                columnResizePolicy = SmartResize.POLICY
                selectionModel.selectedItems.addListener(onSelectionChange)

                setRowFactory(this@VerseBox::rowFactory)
                setOnDragDetected(::dragStart)

                subscribe<DeselectVerses> {
                    if (it.id != this@apply.toString()) {
                        selectionModel.clearSelection()
                    }
                }
            }
            isShowFromTop = false
            isCloseButtonVisible = false
            vboxConstraints { vGrow = Priority.ALWAYS }

            subscribe<SendNotification> {
                showNotification(this@notificationPane, it.message, it.type, it.duration)
            }
        }
        label {
            textProperty().bind(controller.translationProperty.stringBinding {"(${it?.abbreviation ?: ""}) - ${it?.name ?: ""}"})
            textAlignment = TextAlignment.CENTER
        }

        hbox {
            val tf = textfield {
                action { handleText(this)}
                promptText = "Type .h for help"
                hboxConstraints { hGrow = Priority.ALWAYS }
            }
            togglebutton {
                text = "Filter"
                verseSearchController.filterModeProperty.bind(selectedProperty())
                action { handleText(tf) }
            }
        }

        subscribe<RefreshList> {
            displayModel.item = VerseGroup(it.passages.toMutableList())
        }

        subscribe<BroadcastBook> {
            controller.setBookVerses(it.book)
        }

        subscribe<BroadcastTranslation> {
            controller.translationProperty.value = it.translation
            controller.swapVersesByTranslation(it.translation.name)
            verseSearchController.updateBookTrie(it.translation)
        }

        subscribe<BroadcastVBHelp> {
            find<HelpPane>().openWindow(StageStyle.UNIFIED)
        }

    }

    private fun handleText(tf : TextField) {
        if (!tf.text.isNullOrEmpty()) {
            val index = verseSearchController.processText(tf.text, controller.translation, controller.verseList)
            if (controller.verseList.isNotEmpty()) {
                tv.requestResize()
            }
            if (index >= 0) {
                tv.selectionModel.clearAndSelect(index)
                tv.scrollTo(index)
            }
            tf.selectAll()
        }
    }

    private fun showNotification(np : NotificationPane, message: String, type: NotificationType, duration : Int) {
        np.isCloseButtonVisible = false
        when (type) {
            NotificationType.NOTIFICATION -> np.showForSeconds(message, duration = duration)
            NotificationType.WARNING -> np.showForSeconds(message, Styles.fontAwesome.create(FontAwesome.Glyph.WARNING), duration = duration)
            NotificationType.ERROR -> np.showForSeconds(message, Styles.fontAwesome.create(FontAwesome.Glyph.EXCLAMATION_TRIANGLE), duration = duration)
        }
    }

    private fun rowFactory(tv: TableView<Passage>) : TableRow<Passage> {
        return TableRow<Passage>().apply {
            addEventFilter(MouseEvent.MOUSE_PRESSED) {
                if (it.isSecondaryButtonDown) {
                    tv.selectionModel.clearSelection()
                    inGroupModeProperty.value = true
                }
            }
        }
    }

    private fun multiSelectButtonEvent(tv : TableView<Passage>) {
        tv.onKeyPressed = EventHandler {
            when {
                it.isControlDown || it.isShiftDown -> {
                    inGroupModeProperty.value = true
                    if (tv.isHover)
                        tv.scene.cursor = multiCursor
                }
            }
        }
        tv.onKeyReleased = EventHandler {
            when (it.code) {
                KeyCode.CONTROL, KeyCode.SHIFT -> {
                    tv.scene.cursor = Cursor.DEFAULT
                    inGroupModeProperty.value = false
                    fire(DeselectVerses(tv.toString()))
                    fire(RefreshList(tv.selectionModel.selectedItems))
                }
                else -> {}
            }
        }
    }

    private fun dragStart(evt: MouseEvent) {
        dragVerseController.dragStart(evt, tv)
    }
}

fun NotificationPane.showForSeconds(message: String, graphic: Node? = null, duration: Int) {
    if (graphic != null)
        show(message, graphic)
    else
        show(message)

    val pause = PauseTransition(Duration.seconds(duration.toDouble()))
    pause.onFinished = EventHandler { hide() }
    pause.play()
}



    
