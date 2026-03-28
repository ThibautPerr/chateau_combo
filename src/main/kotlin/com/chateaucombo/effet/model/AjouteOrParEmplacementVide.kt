package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.model.Position
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteOrParEmplacementVide")
class AjouteOrParEmplacementVide : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val or = Position.entries.size - context.joueurActuel.tableau.cartesPositionees.size
        logger.info { "Ajout de $or or au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.or += or
    }
}
