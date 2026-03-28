package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParCarteAvecCoutMinimum")
data class PointsParCarteAvecCoutMinimum(val points: Int, val coutMinimum: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees.count { it.carte.cout >= coutMinimum } * points
}
