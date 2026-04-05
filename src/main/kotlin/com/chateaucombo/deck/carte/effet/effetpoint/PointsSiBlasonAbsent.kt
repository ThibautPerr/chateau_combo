package com.chateaucombo.deck.carte.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext

@JsonTypeName("PointsSiBlasonAbsent")
data class PointsSiBlasonAbsent(val points: Int, val blason: Blason) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val blasonPresent = context.joueurActuel.tableau.cartesPositionees
            .any { it.carte.blasons.contains(blason) }
        return if (!blasonPresent) points else 0
    }
}
