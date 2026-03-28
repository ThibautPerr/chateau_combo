package com.chateaucombo.effet.model

import io.github.oshai.kotlinlogging.KotlinLogging

class RemplitBourses(val nb: Int) : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val bourses = context.joueurActuel.tableau.cartesPositionees
            .mapNotNull { it.carte.effetScore as? BourseScore }
            .sortedByDescending { it.taille }
            .take(nb)
        val orDepose = bourses.sumOf { bourse ->
            val orAjoute = bourse.taille - bourse.orDepose
            bourse.orDepose = bourse.taille
            orAjoute
        }
        logger.info { "Joueur ${context.joueurActuel.id} remplit ses bourses et dépose $orDepose or" }
    }
}
