package com.chateaucombo.effet.model

class PointsParOrDepose : EffetScore {
    override fun score(context: ScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees
            .mapNotNull { it.carte.bourse }
            .sumOf { it.orDepose }
}
