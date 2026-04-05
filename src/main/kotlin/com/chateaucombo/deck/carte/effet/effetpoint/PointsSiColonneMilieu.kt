package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.PositionHorizontale

@JsonTypeName("PointsSiColonneMilieu")
data class PointsSiColonneMilieu(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int =
        if (context.cartePositionee.position.positionHorizontale == PositionHorizontale.MILIEU) points else 0
}
