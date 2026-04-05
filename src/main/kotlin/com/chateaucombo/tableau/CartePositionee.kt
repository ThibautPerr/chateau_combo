package com.chateaucombo.tableau

import com.chateaucombo.deck.carte.Carte

data class CartePositionee(
    val carte: Carte,
    val position: Position,
    val tour: Int = 0,
)