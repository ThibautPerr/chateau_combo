package com.chateaucombo.tableau.model

import com.chateaucombo.carte.model.Carte

data class CartePositionee(
    val carte: Carte,
    val position: Position
)