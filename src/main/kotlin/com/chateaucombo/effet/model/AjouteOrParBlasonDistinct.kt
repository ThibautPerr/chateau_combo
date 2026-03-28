package com.chateaucombo.effet.model

import io.github.oshai.kotlinlogging.KotlinLogging

class AjouteOrParBlasonDistinct : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val or = context.joueurActuel.tableau.cartesPositionees
            .flatMap { it.carte.blasons }
            .distinct()
            .size
        logger.info { "Ajout de $or or au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.or += or
    }
}
