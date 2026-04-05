package com.chateaucombo.deck.carte.effet

import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee

data class EffetScoreContext(
    val joueurActuel: Joueur,
    val joueurs: List<Joueur> = emptyList(),
    val cartePositionee: CartePositionee
)
