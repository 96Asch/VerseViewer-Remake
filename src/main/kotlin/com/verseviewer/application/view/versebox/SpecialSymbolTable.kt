package com.verseviewer.application.view.versebox

import com.verseviewer.application.model.SpecialSymbolModel
import tornadofx.*

class SpecialSymbolTable : View("My View") {

    private val symbolModel : SpecialSymbolModel by inject()

    private val charList = listOf(  'à', 'á', 'â', 'ä',
                                    'è', 'é', 'ê', 'ë',
                                    'ì', 'í', 'î', 'ï',
                                    'ò', 'ó', 'ô', 'ö',
                                    'ù', 'ú', 'û', 'ü'
                                    )

    override val root = flowpane {
        charList.map { button(it.toString()) {
                action {
                    symbolModel.symbol.value = it
                }
            }
        }
    }
}
