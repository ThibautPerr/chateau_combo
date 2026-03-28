package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Blason

@JsonTypeName("PointsSiBlasonAbsent")
data class PointsSiBlasonAbsent(val points: Int, val blason: Blason) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val blasonPresent = context.joueurActuel.tableau.cartesPositionees
            .any { it.carte.blasons.contains(blason) }
        return if (!blasonPresent) points else 0
    }
}
