package com.chateaucombo.effet.effetplacement

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Blason
import com.chateaucombo.effet.Effet
import com.chateaucombo.effet.EffetContext
import com.chateaucombo.tableau.model.CartePositionee
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteClePourChaqueBlason")
data class AjouteClePourChaqueBlason(val blason: Blason) : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val cle = context.joueurActuel.tableau.cartesPositionees.sumOf { it.compteBlasons() }
        logger.info { "Ajout de $cle clés au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.cle += cle
    }

    private fun CartePositionee.compteBlasons() = this.carte.blasons.count { it == blason }
}
