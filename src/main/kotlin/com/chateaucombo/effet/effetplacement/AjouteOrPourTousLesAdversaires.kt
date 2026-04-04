package com.chateaucombo.effet.effetplacement

import com.chateaucombo.effet.Effet
import com.chateaucombo.effet.EffetContext
import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteOrPourTousLesAdversaires")
data class AjouteOrPourTousLesAdversaires(val or: Int) : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        context.joueurs
            .filter { it.id != context.joueurActuel.id }
            .forEach { joueur ->
                logger.info { "Ajout de $or or au joueur ${joueur.id}" }
                joueur.or += or
            }
    }
}
