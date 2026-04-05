package com.chateaucombo.deck.carte.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext

@JsonTypeName("PointsParVillageois")
data class PointsParVillageois(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees.count { it.carte is Villageois } * points
}
