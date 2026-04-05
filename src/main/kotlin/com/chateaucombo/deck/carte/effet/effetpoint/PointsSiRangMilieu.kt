package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.PositionVerticale

@JsonTypeName("PointsSiRangMilieu")
data class PointsSiRangMilieu(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int =
        if (context.cartePositionee.position.positionVerticale == PositionVerticale.MILIEU) points else 0
}
