package com.verseviewer.application.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Parent
import tornadofx.Fragment

abstract class RemoveableFragment(val isActivePropery: SimpleBooleanProperty = SimpleBooleanProperty(true)) : Fragment() {

}