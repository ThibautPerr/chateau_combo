package com.chateaucombo.effet.model

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
