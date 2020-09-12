package com.verseviewer.application.model

import tornadofx.*

data class SpecialSymbol (val symbol: Char)

class SpecialSymbolModel : ItemViewModel<SpecialSymbol>() {
    var symbol = bind(SpecialSymbol::symbol)
    var caretPos = 0
}

