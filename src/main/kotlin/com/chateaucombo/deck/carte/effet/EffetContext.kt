package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.Deck
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee

data class EffetContext(
    val joueurActuel: Joueur,
    val joueurs: List<Joueur> = emptyList(),
    val cartePositionee: CartePositionee,
    val decks: List<Deck> = emptyList()
)
