package com.chateaucombo.effet.effetpoint

import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParBlasonDistinct")
data class PointsParBlasonDistinct(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val nbBlasonsDistincts = context.joueurActuel.tableau.cartesPositionees
            .flatMap { it.carte.blasons }
            .distinct()
            .size
        return nbBlasonsDistincts * points
    }
}
