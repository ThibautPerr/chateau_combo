package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParOrDepose")
class PointsParOrDepose : EffetScore {
    override fun score(context: ScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees
            .mapNotNull { it.carte.bourse }
            .sumOf { it.orDepose }
}
