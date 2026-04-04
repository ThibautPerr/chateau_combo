package com.chateaucombo.effet.effetpoint

import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.model.PositionHorizontale
import com.chateaucombo.tableau.model.PositionVerticale

@JsonTypeName("PointsSiCoin")
data class PointsSiCoin(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val position = context.cartePositionee.position
        val estCoin = position.positionVerticale != PositionVerticale.MILIEU &&
                position.positionHorizontale != PositionHorizontale.MILIEU
        return if (estCoin) points else 0
    }
}
