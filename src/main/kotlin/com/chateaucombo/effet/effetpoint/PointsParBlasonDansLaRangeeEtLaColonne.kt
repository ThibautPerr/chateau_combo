package com.chateaucombo.effet.effetpoint

import com.chateaucombo.deck.model.Blason
import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParBlasonDansLaRangeeEtLaColonne")
data class PointsParBlasonDansLaRangeeEtLaColonne(val points: Int, val blason: Blason) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val rangee = context.cartePositionee.position.positionVerticale
        val colonne = context.cartePositionee.position.positionHorizontale
        return context.joueurActuel.tableau.cartesPositionees
            .filter { it.position.positionVerticale == rangee || it.position.positionHorizontale == colonne }
            .sumOf { it.carte.blasons.count { b -> b == blason } } * points
    }
}
