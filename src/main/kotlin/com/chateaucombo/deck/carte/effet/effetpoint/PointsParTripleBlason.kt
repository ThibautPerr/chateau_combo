package com.chateaucombo.deck.carte.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext

@JsonTypeName("PointsParTripleBlason")
data class PointsParTripleBlason(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val tousLesBlasons = context.joueurActuel.tableau.cartesPositionees
            .flatMap { it.carte.blasons }
        return Blason.entries.sumOf { blason ->
            (tousLesBlasons.count { it == blason } / 3) * points
        }
    }
}
