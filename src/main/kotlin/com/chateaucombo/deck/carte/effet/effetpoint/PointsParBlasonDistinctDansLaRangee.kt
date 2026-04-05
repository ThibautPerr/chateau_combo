package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParBlasonDistinctDansLaRangee")
data class PointsParBlasonDistinctDansLaRangee(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val rangee = context.cartePositionee.position.positionVerticale
        val nbDistincts = context.joueurActuel.tableau.cartesPositionees
            .filter { it.position.positionVerticale == rangee }
            .flatMap { it.carte.blasons }
            .distinct()
            .size
        return nbDistincts * points
    }
}
