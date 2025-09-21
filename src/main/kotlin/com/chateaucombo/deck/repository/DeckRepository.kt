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
        return Deck(chatelains) to Deck(villageois)
    }

    private fun List<Carte>.recupereLesChatelains() = this.filterIsInstance<Chatelain>()

    private fun List<Carte>.recupereLesVillageois() = this.filterIsInstance<Villageois>()

    fun melange(deck: Deck): Deck = Deck(deck.cartes.shuffled())
}