package com.chateaucombo.effet.model

import com.chateaucombo.deck.model.Blason
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParBlasonManquant")
data class PointsParBlasonManquant(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val blasonsPresents = context.joueurActuel.tableau.cartesPositionees
            .flatMap { it.carte.blasons }
            .toSet()
        val nbManquants = Blason.entries.count { it !in blasonsPresents }
        return nbManquants * points
    }
}
