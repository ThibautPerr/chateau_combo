package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteClePourTousLesJoueurs")
data class AjouteClePourTousLesJoueurs(val cle: Int) : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        context.joueurs.forEach { joueur ->
            logger.info { "Ajout de $cle clés au joueur ${joueur.id}" }
            joueur.cle += cle
        }
    }
}