package com.chateaucombo.effet.model

import com.chateaucombo.deck.model.Villageois
import io.github.oshai.kotlinlogging.KotlinLogging

class AjouteOrParVillageois : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val or =
            context.joueurActuel.tableau.cartesPositionees.count { cartePositionee -> cartePositionee.carte is Villageois }
        logger.info { "Ajout de $or or au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.or += or
    }
}