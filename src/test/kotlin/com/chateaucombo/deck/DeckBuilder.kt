package com.chateaucombo.deck

import com.chateaucombo.deck.model.Blason.*
import com.chateaucombo.deck.model.Carte
import com.chateaucombo.deck.model.Deck
import com.chateaucombo.deck.model.Villageois

class DeckBuilder {

    fun deckAvecQuatreCartesEtAucuneCarteDisponible() =
        Deck(cartes = mutableListOf(cure(), ecuyer(), epiciere(), fermiere()))

    fun deckAvecTroisCartesEtUneCarteDisponible() =
        Deck(
            cartes = mutableListOf(ecuyer(), epiciere(), fermiere()),
            cartesDisponibles = mutableListOf(cure())
        )

    fun deckAvecDesCartes() =
        Deck(
            cartes = mutableListOf(
                cure(),
                ecuyer(),
                epiciere(),
                fermiere(),
                horlogere(),
                mendiante(),
                milicien()
            )
        )

    fun deckAvecTroisCartesDispos(cartesDisponibles: MutableList<Carte>) =
        Deck(
            cartesDisponibles = cartesDisponibles,
            cartes = mutableListOf(fermiere())
        )

    fun cure(): Carte = Villageois(
        nom = "Curé",
        cout = 0,
        blasons = listOf(RELIGIEUX)
    )

    fun ecuyer(): Carte = Villageois(
        nom = "Écuyer",
        cout = 0,
        blasons = listOf(MILITAIRE)
    )

    fun epiciere(): Carte = Villageois(
        nom = "Épicière",
        cout = 0,
        blasons = listOf(ARTISAN)
    )

    fun fermiere(): Carte = Villageois(
        nom = "Fermière",
        cout = 0,
        blasons = listOf(PAYSAN)
    )

    fun milicien(): Carte = Villageois(
        nom = "Milicien",
        cout = 2,
        blasons = listOf(MILITAIRE)
    )

    private fun mendiante(): Carte = Villageois(
        nom = "Mendiante",
        cout = 0,
        blasons = listOf(PAYSAN)
    )

    fun horlogere(): Carte = Villageois(
        nom = "Horlogère",
        cout = 3,
        blasons = listOf(ARTISAN)
    )

    fun mercenaire(): Carte = Villageois(
        nom = "Mercenaire",
        cout = 6,
        blasons = listOf(PAYSAN, MILITAIRE)
    )
}