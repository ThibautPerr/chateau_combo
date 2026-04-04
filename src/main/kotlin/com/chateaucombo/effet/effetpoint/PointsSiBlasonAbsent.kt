package com.chateaucombo.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Blason
import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext

@JsonTypeName("PointsSiBlasonAbsent")
data class PointsSiBlasonAbsent(val points: Int, val blason: Blason) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val blasonPresent = context.joueurActuel.tableau.cartesPositionees
            .any { it.carte.blasons.contains(blason) }
        return if (!blasonPresent) points else 0
    }
}
