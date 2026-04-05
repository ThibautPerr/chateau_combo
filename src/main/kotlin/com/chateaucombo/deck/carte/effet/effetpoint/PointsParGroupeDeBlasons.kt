package com.chateaucombo.deck.carte.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext

@JsonTypeName("PointsParGroupeDeBlasons")
data class PointsParGroupeDeBlasons(val points: Int, val blasons: List<Blason>) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val tousLesBlasons = context.joueurActuel.tableau.cartesPositionees
            .flatMap { it.carte.blasons }
        val nbGroupes = blasons.minOf { blason -> tousLesBlasons.count { it == blason } }
        return nbGroupes * points
    }
}
