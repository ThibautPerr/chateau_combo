package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Villageois

@JsonTypeName("PointsParVillageois")
data class PointsParVillageois(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees.count { it.carte is Villageois } * points
}
