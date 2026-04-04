package com.chateaucombo.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Chatelain
import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext

@JsonTypeName("PointsParChatelain")
data class PointsParChatelain(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees.count { it.carte is Chatelain } * points
}
