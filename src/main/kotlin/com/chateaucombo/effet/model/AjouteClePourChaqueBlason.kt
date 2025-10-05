package com.chateaucombo.effet.model

import com.chateaucombo.deck.model.Blason
import com.chateaucombo.tableau.model.CartePositionee
import io.github.oshai.kotlinlogging.KotlinLogging

data class AjouteClePourChaqueBlason(val blason: Blason) : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val cle = context.joueurActuel.tableau.cartesPositionees.sumOf { it.compteBlasons() }
        logger.info { "Ajout de $cle clés au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.cle += cle
    }

    private fun CartePositionee.compteBlasons() = this.carte.blasons.count { it == blason }
}
