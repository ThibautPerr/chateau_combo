package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParBlasonDansLaRangeeEtLaColonne")
data class PointsParBlasonDansLaRangeeEtLaColonne(val points: Int, val blason: Blason) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val rangee = context.cartePositionee.position.positionVerticale
        val colonne = context.cartePositionee.position.positionHorizontale
        return context.joueurActuel.tableau.cartesPositionees
            .filter { it.position.positionVerticale == rangee || it.position.positionHorizontale == colonne }
            .sumOf { it.carte.blasons.count { b -> b == blason } } * points
    }
}
