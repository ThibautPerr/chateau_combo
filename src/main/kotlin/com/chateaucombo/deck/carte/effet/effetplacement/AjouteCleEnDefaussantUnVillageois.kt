package com.chateaucombo.deck.carte.effet.effetplacement

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.Effet
import com.chateaucombo.deck.carte.effet.EffetContext
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteCleEnDefaussantUnVillageois")
class AjouteCleEnDefaussantUnVillageois : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val deckVillageois = context.decks.find { deck -> deck.nom == "Villageois" } ?: error("Deck Villageois not found")
        val cartesDisponibles = deckVillageois.cartesDisponibles.filterIsInstance<Villageois>()
        if (cartesDisponibles.isNotEmpty()) {
            val carte = cartesDisponibles.maxBy { it.cout }
            deckVillageois.cartesDisponibles.remove(carte)
            deckVillageois.defausse.addFirst(carte)
            logger.info { "Joueur ${context.joueurActuel.id} défausse '${carte.nom}' (coût ${carte.cout}) et gagne ${carte.cout} clés" }
            context.joueurActuel.cle += carte.cout
        }
    }
}
