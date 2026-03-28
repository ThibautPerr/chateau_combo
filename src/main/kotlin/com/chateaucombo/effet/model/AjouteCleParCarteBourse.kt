package com.chateaucombo.effet.model

import io.github.oshai.kotlinlogging.KotlinLogging

class AjouteCleParCarteBourse : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val cle = context.joueurActuel.tableau.cartesPositionees
            .count { cartePositionee -> cartePositionee.carte.effetScore is BourseScore }
        logger.info { "Ajout de $cle clés au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.cle += cle
    }
}
