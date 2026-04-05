package com.chateaucombo.deck.carte.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext

@JsonTypeName("PointsParTripleVillageois")
data class PointsParTripleVillageois(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val nbVillageois = context.joueurActuel.tableau.cartesPositionees
            .count { it.carte is Villageois }
        return (nbVillageois / 3) * points
    }
}
