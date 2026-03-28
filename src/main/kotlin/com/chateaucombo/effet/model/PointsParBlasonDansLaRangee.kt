package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Blason

@JsonTypeName("PointsParBlasonDansLaRangee")
data class PointsParBlasonDansLaRangee(val points: Int, val blason: Blason) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val rangee = context.cartePositionee.position.positionVerticale
        return context.joueurActuel.tableau.cartesPositionees
            .filter { it.position.positionVerticale == rangee }
            .sumOf { it.carte.blasons.count { b -> b == blason } } * points
    }
}
