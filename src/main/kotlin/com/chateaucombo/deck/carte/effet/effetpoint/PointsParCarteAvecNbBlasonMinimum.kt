package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParCarteAvecNbBlasonMinimum")
data class PointsParCarteAvecNbBlasonMinimum(val points: Int, val nbBlasonMinimum: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees.count { it.carte.blasons.size >= nbBlasonMinimum } * points
}
