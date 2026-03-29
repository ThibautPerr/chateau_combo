package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Villageois

@JsonTypeName("PointsParTripleVillageois")
data class PointsParTripleVillageois(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val nbVillageois = context.joueurActuel.tableau.cartesPositionees
            .count { it.carte is Villageois }
        return (nbVillageois / 3) * points
    }
}
