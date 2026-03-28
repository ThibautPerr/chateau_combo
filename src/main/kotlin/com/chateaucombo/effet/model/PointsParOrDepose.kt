package com.chateaucombo.effet.model

class PointsParOrDepose : EffetScore {
    override fun score(context: ScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees
            .mapNotNull { it.carte.effetScore as? BourseScore }
            .sumOf { it.orDepose }
}
