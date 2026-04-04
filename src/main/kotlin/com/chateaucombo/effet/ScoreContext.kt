package com.chateaucombo.effet

import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee

data class ScoreContext(
    val joueurActuel: Joueur,
    val joueurs: List<Joueur> = emptyList(),
    val cartePositionee: CartePositionee
)
