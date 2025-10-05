package com.chateaucombo.deck.repository

import com.chateaucombo.deck.model.Carte
import com.chateaucombo.deck.model.Chatelain
import com.chateaucombo.deck.model.Deck
import com.chateaucombo.deck.model.Villageois
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

class DeckRepository {

    private val mapper = jacksonObjectMapper()

    fun creeDeuxDecksChatelainsEtVillageoisDepuis(file: File): Pair<Deck, Deck> {
        val cartes = mapper.readValue<List<Carte>>(file)
        val deckChatelains = Deck(nom = "Chatelains", cartes = cartes.recupereLesChatelains().toMutableList(), estLeDeckActuel = false)
        val deckVillageois = Deck(nom = "Villageois", cartes = cartes.recupereLesVillageois().toMutableList(), estLeDeckActuel = true)
        return deckChatelains to deckVillageois
    }

    private fun List<Carte>.recupereLesChatelains() = this.filterIsInstance<Chatelain>()

    private fun List<Carte>.recupereLesVillageois() = this.filterIsInstance<Villageois>()

    fun melange(deck: Deck) {
        deck.cartes.shuffle()
    }

    fun remplitLesCartesDisponibles(deck: Deck) {
        if (deck.cartesDisponibles.size < 3)
            remplitTroisCartesDisponibles(deck)
    }

    private fun remplitTroisCartesDisponibles(deck: Deck) {
        (1..(3 - deck.cartesDisponibles.size)).forEach { _ ->
            if (deck.cartes.isEmpty()) {
                remplitLeDeckAvecLaDefausse(deck)
            }
            deck.cartesDisponibles += deck.cartes.first()
            deck.cartes.removeFirst()
        }
    }

    private fun remplitLeDeckAvecLaDefausse(deck: Deck) {
        val cartesDefaussees = deck.defausse
        cartesDefaussees.shuffle()
        cartesDefaussees.forEach { carte -> deck.cartes.addLast(carte) }
        deck.defausse.clear()
    }

    fun rafraichitLeDeck(deck: Deck) {
        deck.cartesDisponibles.forEach { carte -> deck.defausse.addFirst(carte) }
        deck.cartesDisponibles.clear()
        remplitLesCartesDisponibles(deck)
    }
}