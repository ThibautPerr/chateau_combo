package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParBlasonDistinctDansLaColonne")
data class PointsParBlasonDistinctDansLaColonne(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val colonne = context.cartePositionee.position.positionHorizontale
        val nbDistincts = context.joueurActuel.tableau.cartesPositionees
            .filter { it.position.positionHorizontale == colonne }
            .flatMap { it.carte.blasons }
            .distinct()
            .size
        return nbDistincts * points
    }
}
