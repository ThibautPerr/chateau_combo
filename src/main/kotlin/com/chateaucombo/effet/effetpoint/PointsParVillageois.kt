package com.chateaucombo.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext

@JsonTypeName("PointsParVillageois")
data class PointsParVillageois(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees.count { it.carte is Villageois } * points
}
