package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParBlasonManquant")
data class PointsParBlasonManquant(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val blasonsPresents = context.joueurActuel.tableau.cartesPositionees
            .flatMap { it.carte.blasons }
            .toSet()
        val nbManquants = Blason.entries.count { it !in blasonsPresents }
        return nbManquants * points
    }
}
