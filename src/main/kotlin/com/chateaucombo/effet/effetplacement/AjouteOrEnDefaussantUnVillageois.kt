package com.chateaucombo.effet.effetplacement

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.effet.Effet
import com.chateaucombo.effet.EffetContext
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteOrEnDefaussantUnVillageois")
class AjouteOrEnDefaussantUnVillageois : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val deckVillageois = context.decks.find { deck -> deck.nom == "Villageois" } ?: error("Deck Villageois not found")
        val cartesDisponibles = deckVillageois.cartesDisponibles.filterIsInstance<Villageois>()
        if (cartesDisponibles.isNotEmpty()) {
            val carte = cartesDisponibles.maxBy { it.cout }
            deckVillageois.cartesDisponibles.remove(carte)
            deckVillageois.defausse.addFirst(carte)
            logger.info { "Joueur ${context.joueurActuel.id} défausse '${carte.nom}' (coût ${carte.cout}) et gagne ${carte.cout} or" }
            context.joueurActuel.or += carte.cout
        }
    }
}
