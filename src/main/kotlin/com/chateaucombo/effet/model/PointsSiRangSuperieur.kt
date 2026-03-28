package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.model.PositionVerticale

@JsonTypeName("PointsSiRangSuperieur")
data class PointsSiRangSuperieur(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        if (context.cartePositionee.position.positionVerticale == PositionVerticale.HAUT) points else 0
}
