package com.chateaucombo.effet.effetpoint

import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParBlasonDistinctDansLaColonne")
data class PointsParBlasonDistinctDansLaColonne(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val colonne = context.cartePositionee.position.positionHorizontale
        val nbDistincts = context.joueurActuel.tableau.cartesPositionees
            .filter { it.position.positionHorizontale == colonne }
            .flatMap { it.carte.blasons }
            .distinct()
            .size
        return nbDistincts * points
    }
}
