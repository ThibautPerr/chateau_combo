package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParBlasonDistinct")
data class PointsParBlasonDistinct(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val nbBlasonsDistincts = context.joueurActuel.tableau.cartesPositionees
            .flatMap { it.carte.blasons }
            .distinct()
            .size
        return nbBlasonsDistincts * points
    }
}
