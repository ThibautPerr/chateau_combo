package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Chatelain
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteOrEnDefaussantUnChatelain")
class AjouteOrEnDefaussantUnChatelain : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val deckChatelains = context.decks.find { deck -> deck.nom == "Chatelains" } ?: error("Deck Chatelains not found")
        val cartesDisponibles = deckChatelains.cartesDisponibles.filterIsInstance<Chatelain>()
        if (cartesDisponibles.isNotEmpty()) {
            val carte = cartesDisponibles.maxBy { it.cout }
            deckChatelains.cartesDisponibles.remove(carte)
            deckChatelains.defausse.addFirst(carte)
            logger.info { "Joueur ${context.joueurActuel.id} défausse '${carte.nom}' (coût ${carte.cout}) et gagne ${carte.cout} or" }
            context.joueurActuel.or += carte.cout
        }
    }
}
