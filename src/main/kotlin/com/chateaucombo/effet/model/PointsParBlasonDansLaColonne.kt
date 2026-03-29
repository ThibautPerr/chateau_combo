package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Blason

@JsonTypeName("PointsParBlasonDansLaColonne")
data class PointsParBlasonDansLaColonne(val points: Int, val blason: Blason) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val colonne = context.cartePositionee.position.positionHorizontale
        return context.joueurActuel.tableau.cartesPositionees
            .filter { it.position.positionHorizontale == colonne }
            .sumOf { it.carte.blasons.count { b -> b == blason } } * points
    }
}
