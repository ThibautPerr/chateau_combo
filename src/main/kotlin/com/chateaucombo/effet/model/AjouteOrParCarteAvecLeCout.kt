package com.chateaucombo.effet.model

import io.github.oshai.kotlinlogging.KotlinLogging

data class AjouteOrParCarteAvecLeCout(
    val orParCarte: Int,
    val cout: Int
) : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val or = orParCarte * context.joueurActuel.tableau.cartesPositionees.count { it.carte.cout == cout }
        logger.info { "Ajout de $or or au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.or += or
    }

}
