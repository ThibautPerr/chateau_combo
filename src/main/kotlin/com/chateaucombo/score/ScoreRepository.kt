package com.chateaucombo.score

import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.chateaucombo.joueur.Joueur
import io.github.oshai.kotlinlogging.KotlinLogging

class ScoreRepository {
    private val logger = KotlinLogging.logger { }

    fun compteLesScores(joueurs: List<Joueur>) {
        joueurs.forEach { joueur ->
            joueur.remplitLesBourses()
            joueur.updateScoreWithEffects(joueurs)
            joueur.updateScoreWithBourses()
            joueur.updateScoreWithCles()
        }
    }

    private fun Joueur.remplitLesBourses() {
        val bourses = this.tableau.cartesPositionees
            .mapNotNull { it.carte.bourse }
        var orRestant = this.or
        bourses.forEach { bourse ->
            val orMis = minOf(orRestant, bourse.taille - bourse.orDepose)
            orRestant -= orMis
            bourse.orDepose += orMis
        }
    }

    private fun Joueur.updateScoreWithEffects(joueurs: List<Joueur>) {
        this.score = this.tableau.cartesPositionees.sumOf { cartePositionee ->
            val context = EffetScoreContext(joueurActuel = this, joueurs = joueurs, cartePositionee = cartePositionee)
            val points = cartePositionee.carte.effetScore.score(context)
            logger.debug { "Joueur ${this.id} : ${cartePositionee.carte.nom} rapporte $points point(s)" }
            points
        }
    }

    private fun Joueur.updateScoreWithCles() {
        if (this.cle > 0) {
            logger.debug { "Joueur ${this.id} : ${this.cle} clé(s) rapporte(nt) ${this.cle} point(s)" }
            this.score += this.cle
        }
    }

    private fun Joueur.updateScoreWithBourses() {
        this.score += this.tableau.cartesPositionees
            .filter { it.carte.bourse != null }
            .sumOf { cartePositionee ->
                val points = cartePositionee.carte.bourse!!.orDepose * 2
                if (points != 0)
                    logger.debug { "Joueur ${this.id} : ${cartePositionee.carte.nom} (bourse) rapporte $points point(s)" }
                points
            }
    }
}
