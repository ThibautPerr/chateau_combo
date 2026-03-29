package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Blason

@JsonTypeName("PointsParTripleBlason")
data class PointsParTripleBlason(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val tousLesBlasons = context.joueurActuel.tableau.cartesPositionees
            .flatMap { it.carte.blasons }
        return Blason.entries.sumOf { blason ->
            (tousLesBlasons.count { it == blason } / 3) * points
        }
    }
}
