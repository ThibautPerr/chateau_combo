package com.chateaucombo.deck.carte.effet.effetplacement

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.carte.effet.Effet
import com.chateaucombo.deck.carte.effet.EffetContext
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteCleParChatelain")
class AjouteCleParChatelain : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val cle = context.joueurActuel.tableau.cartesPositionees.count { cartePositionee -> cartePositionee.carte is Chatelain }
        logger.info { "Ajout de $cle clés au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.cle += cle
    }
}