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
        val chatelains = cartes.recupereLesChatelains()
        val villageois = cartes.recupereLesVillageois()
        return Deck(cartes = chatelains) to Deck(cartes = villageois)
    }

    private fun List<Carte>.recupereLesChatelains() = this.filterIsInstance<Chatelain>()

    private fun List<Carte>.recupereLesVillageois() = this.filterIsInstance<Villageois>()

    fun melange(deck: Deck): Deck = Deck(cartes = deck.cartes.shuffled())

    fun remplitLesCartesDisponibles(deck: Deck): Deck {
        return when (deck.cartesDisponibles.size < 3) {
            true -> remplitTroisCartesDisponibles(deck)
            false -> deck
        }
    }

    private fun remplitTroisCartesDisponibles(deck: Deck): Deck {
        val nouvellesCartes = deck.cartes.toMutableList()
        val nouvellesCartesDisponibles = deck.cartesDisponibles.toMutableList()
        (1..(3 - deck.cartesDisponibles.size)).forEach { _ ->
            nouvellesCartesDisponibles += nouvellesCartes.first()
            nouvellesCartes.removeFirst()
        }
        return Deck(cartes = nouvellesCartes.toList(), cartesDisponibles = nouvellesCartesDisponibles.toList())
    }
}