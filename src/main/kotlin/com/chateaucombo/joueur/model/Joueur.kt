package com.chateaucombo.joueur.model

import com.chateaucombo.tableau.model.Tableau

data class Joueur(
    val id: Int,
    var or: Int = 15,
    var cle: Int = 2,
    val tableau: Tableau = Tableau()
)