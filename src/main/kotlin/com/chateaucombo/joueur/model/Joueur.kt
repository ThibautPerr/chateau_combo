package com.chateaucombo.joueur.model

import com.chateaucombo.tableau.model.Tableau

data class Joueur(
    val id: Int,
    val or: Int = 15,
    val cle: Int = 2,
    val tableau: Tableau = Tableau()
)