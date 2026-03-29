package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Chatelain

@JsonTypeName("PointsParChatelain")
data class PointsParChatelain(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees.count { it.carte is Chatelain } * points
}
