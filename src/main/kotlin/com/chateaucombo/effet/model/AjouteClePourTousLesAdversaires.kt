package com.chateaucombo.effet.model

import io.github.oshai.kotlinlogging.KotlinLogging

data class AjouteClePourTousLesAdversaires(val cle: Int) : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        context.joueurs
            .filter { it.id != context.joueurActuel.id }
            .forEach { joueur ->
                logger.info { "Ajout de $cle clés au joueur ${joueur.id}" }
                joueur.cle += cle
            }
    }
}