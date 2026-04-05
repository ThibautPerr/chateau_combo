package com.chateaucombo.deck.carte.effet.effetplacement

import com.chateaucombo.deck.carte.effet.Effet
import com.chateaucombo.deck.carte.effet.EffetContext
import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteOrParCartePositionee")
class AjouteOrParCartePositionee : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val or = context.joueurActuel.tableau.cartesPositionees.size
        logger.info { "Ajout de $or or au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.or += or
    }
}
