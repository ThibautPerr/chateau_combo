package com.chateaucombo.effet.model

import io.github.oshai.kotlinlogging.KotlinLogging

class RemplitBourses(val nb: Int) : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val orGagne = context.joueurActuel.tableau.cartesPositionees
            .mapNotNull { (it.carte.effetScore as? BourseScore)?.taille }
            .sortedDescending()
            .take(nb)
            .sum()
        logger.info { "Joueur ${context.joueurActuel.id} remplit ses bourses et dépose $orGagne or" }
        context.joueurActuel.orBourses += orGagne
    }
}
