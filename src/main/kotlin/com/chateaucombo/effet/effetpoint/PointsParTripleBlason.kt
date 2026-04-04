package com.chateaucombo.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Blason
import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext

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
