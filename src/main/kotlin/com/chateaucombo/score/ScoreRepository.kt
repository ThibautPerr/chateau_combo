package com.chateaucombo.score

import com.chateaucombo.effet.model.BourseScore
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur

class ScoreRepository {
    fun compteLeScore(joueurs: List<Joueur>) {
        joueurs.forEach { joueur ->
            joueur.score = joueur.tableau.cartesPositionees.sumOf { cartePositionee ->
                val context = ScoreContext(joueurActuel = joueur, joueurs = joueurs, carte = cartePositionee.carte)
                cartePositionee.carte.effetScore.score(context)
            }
            val capaciteTotaleBourse = joueur.tableau.cartesPositionees
                .mapNotNull { (it.carte.effetScore as? BourseScore)?.taille }
                .sum()
            joueur.score += minOf(joueur.orBourses + joueur.or, capaciteTotaleBourse) * 2
        }
    }
}
