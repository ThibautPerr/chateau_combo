package com.chateaucombo.joueur

import com.chateaucombo.ReglesDuJeu
import com.chateaucombo.strategie.Strategie
import com.chateaucombo.strategie.StrategieAleatoire
import com.chateaucombo.tableau.Tableau

data class Joueur(
    val id: Int,
    var or: Int = ReglesDuJeu.OR_INITIAL,
    var cle: Int = ReglesDuJeu.CLES_INITIALES,
    val tableau: Tableau = Tableau(),
    var score: Int = 0,
    val strategie: Strategie = StrategieAleatoire()
)