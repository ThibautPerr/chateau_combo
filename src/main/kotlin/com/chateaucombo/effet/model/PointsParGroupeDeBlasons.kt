package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Blason

@JsonTypeName("PointsParGroupeDeBlasons")
data class PointsParGroupeDeBlasons(val points: Int, val blasons: List<Blason>) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val tousLesBlasons = context.joueurActuel.tableau.cartesPositionees
            .flatMap { it.carte.blasons }
        val nbGroupes = blasons.minOf { blason -> tousLesBlasons.count { it == blason } }
        return nbGroupes * points
    }
}
