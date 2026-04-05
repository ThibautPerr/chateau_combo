package com.chateaucombo.deck.carte.effet.effetplacement

import com.chateaucombo.deck.carte.effet.Effet
import com.chateaucombo.deck.carte.effet.EffetContext
import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteCle")
data class AjouteCle(val cle: Int) : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        logger.info { "Ajout de $cle clés au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.cle += cle
    }
}