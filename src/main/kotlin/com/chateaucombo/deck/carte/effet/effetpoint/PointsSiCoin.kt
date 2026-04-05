package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.PositionHorizontale
import com.chateaucombo.tableau.PositionVerticale

@JsonTypeName("PointsSiCoin")
data class PointsSiCoin(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val position = context.cartePositionee.position
        val estCoin = position.positionVerticale != PositionVerticale.MILIEU &&
                position.positionHorizontale != PositionHorizontale.MILIEU
        return if (estCoin) points else 0
    }
}
