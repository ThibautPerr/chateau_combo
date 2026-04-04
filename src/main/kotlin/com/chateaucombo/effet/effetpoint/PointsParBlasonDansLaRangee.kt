package com.chateaucombo.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Blason
import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext

@JsonTypeName("PointsParBlasonDansLaRangee")
data class PointsParBlasonDansLaRangee(val points: Int, val blason: Blason) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val rangee = context.cartePositionee.position.positionVerticale
        return context.joueurActuel.tableau.cartesPositionees
            .filter { it.position.positionVerticale == rangee }
            .sumOf { it.carte.blasons.count { b -> b == blason } } * points
    }
}
