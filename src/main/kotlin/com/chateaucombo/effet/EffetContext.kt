package com.chateaucombo.effet

import com.chateaucombo.deck.model.Deck
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee

data class EffetContext(
    val joueurActuel: Joueur,
    val joueurs: List<Joueur> = emptyList(),
    val cartePositionee: CartePositionee,
    val decks: List<Deck> = emptyList()
)
