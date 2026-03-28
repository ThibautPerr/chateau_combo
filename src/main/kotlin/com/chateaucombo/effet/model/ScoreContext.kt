package com.chateaucombo.effet.model

import com.chateaucombo.deck.model.Carte
import com.chateaucombo.joueur.model.Joueur

data class ScoreContext(
    val joueurActuel: Joueur,
    val joueurs: List<Joueur> = emptyList(),
    val carte: Carte
)
