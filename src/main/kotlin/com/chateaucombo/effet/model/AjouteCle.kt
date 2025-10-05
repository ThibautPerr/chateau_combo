package com.chateaucombo.effet.model

import io.github.oshai.kotlinlogging.KotlinLogging

data class AjouteCle(val cle: Int) : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        logger.info { "Ajout de $cle clés au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.cle += cle
    }
}