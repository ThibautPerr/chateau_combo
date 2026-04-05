package com.chateaucombo.deck.carte.effet.effetplacement

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.effet.Effet
import com.chateaucombo.deck.carte.effet.EffetContext
import com.chateaucombo.tableau.CartePositionee
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
