package com.chateaucombo.deck

import com.chateaucombo.deck.carte.Carte

data class Deck(
    val nom: String = "",
    val cartes: MutableList<Carte>,
    val cartesDisponibles: MutableList<Carte> = mutableListOf(),
    val defausse: MutableList<Carte> = mutableListOf(),
    var estLeDeckActuel: Boolean
)