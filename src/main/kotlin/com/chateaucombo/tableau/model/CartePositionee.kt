package com.chateaucombo.tableau.model

import com.chateaucombo.deck.model.Carte

data class CartePositionee(
    val carte: Carte,
    val position: Position
)