package com.chateaucombo.deck.carte.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext

@JsonTypeName("PointsParChatelain")
data class PointsParChatelain(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees.count { it.carte is Chatelain } * points
}
