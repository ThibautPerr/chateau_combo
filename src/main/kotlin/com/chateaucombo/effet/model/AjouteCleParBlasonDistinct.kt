package com.chateaucombo.effet.model

import io.github.oshai.kotlinlogging.KotlinLogging

class AjouteCleParBlasonDistinct : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val cle = context.joueurActuel.tableau.cartesPositionees
            .flatMap { it.carte.blasons }
            .distinct()
            .size
        logger.info { "Ajout de $cle clés au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.cle += cle
    }
}
