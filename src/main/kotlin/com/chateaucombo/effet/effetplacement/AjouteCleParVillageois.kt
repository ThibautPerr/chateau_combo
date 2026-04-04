package com.chateaucombo.effet.effetplacement

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.effet.Effet
import com.chateaucombo.effet.EffetContext
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteCleParVillageois")
class AjouteCleParVillageois : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val cle =
            context.joueurActuel.tableau.cartesPositionees.count { cartePositionee -> cartePositionee.carte is Villageois }
        logger.info { "Ajout de $cle clés au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.cle += cle
    }
}