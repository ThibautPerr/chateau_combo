package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParCarteAvecNbBlasonMinimum")
data class PointsParCarteAvecNbBlasonMinimum(val points: Int, val nbBlasonMinimum: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees.count { it.carte.blasons.size >= nbBlasonMinimum } * points
}
