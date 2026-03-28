package com.chateaucombo.joueur.model

import com.chateaucombo.ReglesDuJeu
import com.chateaucombo.tableau.model.Tableau

data class Joueur(
    val id: Int,
    var or: Int = ReglesDuJeu.OR_INITIAL,
    var cle: Int = ReglesDuJeu.CLES_INITIALES,
    val tableau: Tableau = Tableau(),
    var score: Int = 0
)