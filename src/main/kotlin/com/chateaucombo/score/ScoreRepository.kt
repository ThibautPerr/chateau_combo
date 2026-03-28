package com.chateaucombo.score

import com.chateaucombo.effet.model.BourseScore
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur

class ScoreRepository {
    fun compteLeScore(joueurs: List<Joueur>) {
        joueurs.forEach { joueur ->
            joueur.remplitLesBourses()
            joueur.updateScoreWithEffects(joueurs)
            joueur.updateScoreWithBourses()
        }
    }

    private fun Joueur.remplitLesBourses() {
        val bourses = this.tableau.cartesPositionees
            .mapNotNull { it.carte.effetScore as? BourseScore }
        var orRestant = this.or
        bourses.forEach { bourse ->
            val orMis = minOf(orRestant, bourse.taille - bourse.orDepose)
            orRestant -= orMis
            bourse.orDepose += orMis
        }
    }

    private fun Joueur.updateScoreWithEffects(joueurs: List<Joueur>) {
        this.score = this.tableau.cartesPositionees.sumOf { cartePositionee ->
            val context = ScoreContext(joueurActuel = this, joueurs = joueurs, cartePositionee = cartePositionee)
            cartePositionee.carte.effetScore.score(context)
        }
    }

    private fun Joueur.updateScoreWithBourses() {
        this.score += this.tableau.cartesPositionees
            .mapNotNull { it.carte.effetScore as? BourseScore }
            .sumOf { bourse -> bourse.orDepose * 2 }
    }
}
