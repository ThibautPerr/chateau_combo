package com.chateaucombo.effet.model

import com.chateaucombo.deck.model.Chatelain
import io.github.oshai.kotlinlogging.KotlinLogging

class AjouteCleParChatelain : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val cle = context.joueurActuel.tableau.cartesPositionees.count { cartePositionee -> cartePositionee.carte is Chatelain }
        logger.info { "Ajout de $cle clés au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.cle += cle
    }
}