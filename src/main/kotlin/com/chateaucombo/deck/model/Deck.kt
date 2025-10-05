package com.chateaucombo.deck.model

data class Deck(
    val cartes: MutableList<Carte>,
    val cartesDisponibles: MutableList<Carte> = mutableListOf(),
    val defausse: MutableList<Carte> = mutableListOf(),
)