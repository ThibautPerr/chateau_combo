package com.chateaucombo.deck

import com.chateaucombo.deck.model.Blason.*
import com.chateaucombo.deck.model.Carte
import com.chateaucombo.deck.model.Deck
import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.effet.model.Effets

class DeckBuilder {

    fun deckAvecQuatreCartesEtAucuneCarteDisponible() =
        Deck(cartes = mutableListOf(cure(), ecuyer(), epiciere(), fermiere()), estLeDeckActuel = true)

    fun deckAvecTroisCartesEtUneCarteDisponible() =
        Deck(
            cartes = mutableListOf(ecuyer(), epiciere(), fermiere()),
            cartesDisponibles = mutableListOf(cure()),
            estLeDeckActuel = true
        )

    fun deckAvecDesCartes(estLeDeckActuel: Boolean = true) =
        Deck(
            cartes = mutableListOf(
                cure(),
                ecuyer(),
                epiciere(),
                fermiere(),
                horlogere(),
                mendiante(),
                milicien()
            ),
            estLeDeckActuel = estLeDeckActuel
        )

    fun deckAvecTroisCartesDispos(
        cartesDisponibles: List<Carte>,
        cartes: List<Carte> = listOf(fermiere(), milicien(), mendiante())
    ) =
        Deck(
            cartesDisponibles = cartesDisponibles.toMutableList(),
            cartes = cartes.toMutableList(),
            estLeDeckActuel = true
        )

    fun cure(): Carte = Villageois(
        nom = "Curé",
        cout = 0,
        blasons = listOf(RELIGIEUX),
        effets = Effets()
    )

    fun ecuyer(): Carte = Villageois(
        nom = "Écuyer",
        cout = 0,
        blasons = listOf(MILITAIRE),
        effets = Effets()
    )

    fun epiciere(): Carte = Villageois(
        nom = "Épicière",
        cout = 0,
        blasons = listOf(ARTISAN),
        effets = Effets()
    )

    fun fermiere(): Carte = Villageois(
        nom = "Fermière",
        cout = 0,
        blasons = listOf(PAYSAN),
        effets = Effets()
    )

    fun milicien(): Carte = Villageois(
        nom = "Milicien",
        cout = 2,
        blasons = listOf(MILITAIRE),
        effets = Effets()
    )

    fun mendiante(): Carte = Villageois(
        nom = "Mendiante",
        cout = 0,
        blasons = listOf(PAYSAN),
        effets = Effets()
    )

    fun horlogere(): Carte = Villageois(
        nom = "Horlogère",
        cout = 3,
        blasons = listOf(ARTISAN),
        effets = Effets()
    )

    fun mercenaire(): Carte = Villageois(
        nom = "Mercenaire",
        cout = 6,
        blasons = listOf(PAYSAN, MILITAIRE),
        effets = Effets()
    )
}