package com.example.demo.view.versebox

import com.example.demo.controller.DragVerseController
import com.example.demo.controller.VerseSearchController
import com.example.demo.model.*
import com.example.demo.model.datastructure.GroupType
import com.example.demo.model.datastructure.VerseGroup
import com.example.demo.model.event.*
import javafx.animation.PauseTransition
import javafx.beans.Observable
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.control.TreeTableRow
import javafx.scene.input.*
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import javafx.util.Duration
import org.controlsfx.control.NotificationPane
import tornadofx.*
import tornadofx.controlsfx.maskerpane
import tornadofx.controlsfx.notificationPane
import tornadofx.controlsfx.maskerpane as maskerpane1


class VerseBox : Fragment() {
    private val verseModel : VerseModel by inject()
    private val tableModel : TableVersesModel by inject()
    private val displayModel : DisplayVersesModel by inject()
    private val translationModel: TranslationModel by inject()

    private val dragVerseController : DragVerseController by inject()
    private val verseSearchController : VerseSearchController by inject()

    private val helpPane : HelpPane by inject()
    private val tv = tableview(tableModel.verses)


    private val multiCursor = Cursor.CROSSHAIR
    private var inGroupModeProperty = SimpleBooleanProperty(false)
    private val displayLimit = 10

    private val warningIm = imageview("icons/warning.png")
    private val errorIm = imageview("icons/error.png")

    override val root = vbox {
        label(translationModel.name) {
            textAlignment = TextAlignment.CENTER
        }

        val np = notificationPane {
            content = tv.apply {
                readonlyColumn("B", Verse::book).isSortable = false
                readonlyColumn("C", Verse::chapter).isSortable = false
                readonlyColumn("V", Verse::verse).isSortable = false
                column("Text", Verse::textProperty).enableTextWrap().remainingWidth().isSortable = false

                onUserSelect {
                    openInternalWindow<VerseEditor>(escapeClosesWindow = true)
                }
                bindSelected(verseModel)
                multiSelect(true)
                multiSelectButtonEvent(tv)
                hoverProperty().and(inGroupModeProperty).addListener { _, _, new ->
                    if (new) {
                        scene.cursor = multiCursor
                    }
                    else {
                        scene.cursor = Cursor.DEFAULT
                    }
                }
                columnResizePolicy = SmartResize.POLICY
                selectionModel.selectedItems.addListener(onSelectionChange())
                tableModel.verses.addListener(ListChangeListener { change ->
                    if (change.list.isNotEmpty())
                        selectionModel.selectFirst()
                })
                setRowFactory(this@VerseBox::rowFactory)
                setOnDragDetected(::dragStart)
            }
            isShowFromTop = false
            isCloseButtonVisible = false
            vboxConstraints { vGrow = Priority.ALWAYS }

        }

        textfield {
            action {
                if (!text.isNullOrEmpty()) {
                    val verses = verseSearchController.processText(text)
                    if (verses != null) {
                        tableModel.verses.setAll(verses)
                        tv.requestResize()
                    }
                    selectAll()
                }
            }
            promptText = "Type .h for help"
        }

        subscribe<SendNotification> {
            showNotification(np, it.message, it.type, it.duration, it.node)
        }

        subscribe<RefreshList>() {
            displayModel.rebind{ group = VerseGroup(it.verses.toMutableList(), it.type) }
            displayModel.commit()
            tv.requestResize()
        }

        displayModel.groupProperty.addListener { _ : Observable ->
            println("Changed: " + displayModel.group)
        }
    }

    private fun showNotification(np : NotificationPane, message: String, type: NotificationType, duration : Int, node: Node?) {
        np.isCloseButtonVisible = false
        when (type) {
            NotificationType.NOTIFICATION -> np.showForSeconds(message, duration = duration)
            NotificationType.WARNING -> np.showForSeconds(message, warningIm, duration = duration)
            NotificationType.ERROR -> np.showForSeconds(message, errorIm, duration = duration)
            NotificationType.HELP -> {
                np.isCloseButtonVisible = true
                np.show(null, helpPane.root, null)
            }

        }
    }

    private fun rowFactory(tv: TableView<Verse>) : TableRow<Verse> {
        return TableRow<Verse>().apply {
            addEventFilter(MouseEvent.MOUSE_PRESSED) {
                if (it.isSecondaryButtonDown) {
                    tv.selectionModel.clearSelection()
                    inGroupModeProperty.value = true
                }
            }
        }
    }

    
    private fun multiSelectButtonEvent(tv : TableView<Verse>) {
        tv.onKeyPressed = EventHandler {
            print(it.code.getName() + " pressed")

            when {
                it.isControlDown || it.isShiftDown -> {
                    inGroupModeProperty.value = true
                    if (tv.isHover)
                        tv.scene.cursor = multiCursor
                }
            }
        }
        tv.onKeyReleased = EventHandler {
            println(it.code.getName() + " released")

            when (it.code) {
                KeyCode.CONTROL, KeyCode.SHIFT -> {
                    tv.scene.cursor = Cursor.DEFAULT
                    inGroupModeProperty.value = false
                    fire(RefreshList(tv.selectionModel.selectedItems, GroupType.MONO_TRANSLATION))
                }
                else -> {}
            }
        }
    }

    private fun dragStart(evt: MouseEvent) {
        dragVerseController.dragStart(evt, tv)
    }


    private fun onSelectionChange() : ListChangeListener<Verse> = ListChangeListener<Verse> { changed ->
        if (changed.list.isEmpty().not() && !inGroupModeProperty.value && changed.list.size < displayLimit) {
            displayModel.rebind{ group = VerseGroup(changed.list.toMutableList(), GroupType.MONO_TRANSLATION) }
        }
    }

}

fun NotificationPane.showForSeconds(message: String, graphic: Node? = null, duration: Int) {
    if (graphic != null) {
        show(message, graphic)
    }
    else {
        show(message)
    }

    val pause = PauseTransition(Duration.seconds(duration.toDouble()))
    pause.onFinished = EventHandler {
        hide()
    }
    pause.play()
}



    
