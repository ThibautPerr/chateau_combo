package com.chateaucombo.effet.model

import com.chateaucombo.deck.model.Carte
import com.chateaucombo.deck.model.Deck
import com.chateaucombo.joueur.model.Joueur

data class EffetContext(
    val joueurActuel: Joueur,
    val joueurs: List<Joueur> = emptyList(),
    val carte: Carte,
    val decks: List<Deck> = emptyList()
)
