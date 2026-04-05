package com.chateaucombo.deck.carte.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext

@JsonTypeName("PointsParBlasonDansLaColonne")
data class PointsParBlasonDansLaColonne(val points: Int, val blason: Blason) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val colonne = context.cartePositionee.position.positionHorizontale
        return context.joueurActuel.tableau.cartesPositionees
            .filter { it.position.positionHorizontale == colonne }
            .sumOf { it.carte.blasons.count { b -> b == blason } } * points
    }
}
