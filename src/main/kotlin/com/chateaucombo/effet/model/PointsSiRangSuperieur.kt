package com.chateaucombo.effet.model

import com.chateaucombo.tableau.model.PositionVerticale

data class PointsSiRangSuperieur(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        if (context.cartePositionee.position.positionVerticale == PositionVerticale.HAUT) points else 0
}
