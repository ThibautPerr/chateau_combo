package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParCarteAvecCoutExact")
data class PointsParCarteAvecCoutExact(val points: Int, val cout: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees.count { it.carte.cout == cout } * points
}
