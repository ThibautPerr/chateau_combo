package com.chateaucombo.joueur.model

import com.chateaucombo.card.model.Carte

data class Joueur(
    val id: Int,
    val or: Int = 15,
    val cle: Int = 2,
    val cartes: MutableList<Carte> = mutableListOf()
)