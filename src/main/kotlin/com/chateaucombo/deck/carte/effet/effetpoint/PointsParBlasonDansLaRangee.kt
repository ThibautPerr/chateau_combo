package com.chateaucombo.deck.carte.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext

@JsonTypeName("PointsParBlasonDansLaRangee")
data class PointsParBlasonDansLaRangee(val points: Int, val blason: Blason) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val rangee = context.cartePositionee.position.positionVerticale
        return context.joueurActuel.tableau.cartesPositionees
            .filter { it.position.positionVerticale == rangee }
            .sumOf { it.carte.blasons.count { b -> b == blason } } * points
    }
}
