package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParCarteAvecCoutMinimum")
data class PointsParCarteAvecCoutMinimum(val points: Int, val coutMinimum: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees.count { it.carte.cout >= coutMinimum } * points
}
