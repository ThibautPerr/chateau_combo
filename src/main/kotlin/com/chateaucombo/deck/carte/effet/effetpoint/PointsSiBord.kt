package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.PositionHorizontale
import com.chateaucombo.tableau.PositionVerticale

@JsonTypeName("PointsSiBord")
data class PointsSiBord(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val position = context.cartePositionee.position
        val estBord = (position.positionVerticale == PositionVerticale.MILIEU) xor
                (position.positionHorizontale == PositionHorizontale.MILIEU)
        return if (estBord) points else 0
    }
}
