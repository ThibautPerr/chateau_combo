package com.chateaucombo.score

import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.effet.model.AjoutePoints
import com.chateaucombo.effet.model.EffetScoreVide
import com.chateaucombo.effet.model.Effets
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ScoreRepositoryTest {
    private val scoreRepository = ScoreRepository()

    private fun villageois(effetScore: com.chateaucombo.effet.model.EffetScore = EffetScoreVide) =
        Villageois(cout = 0, nom = "carte", blasons = emptyList(), effets = Effets(), effetScore = effetScore)

    @Test
    fun `doit compter les points d'un joueur avec une carte qui rapporte 5 points`() {
        val carte = villageois(effetScore = AjoutePoints(5))
        val tableau = Tableau(cartesPositionees = mutableListOf(CartePositionee(carte = carte, position = HAUTGAUCHE)))
        val joueur = Joueur(id = 1, tableau = tableau)

        scoreRepository.compteLeScore(listOf(joueur))

        assertThat(joueur.score).isEqualTo(5)
    }

    @Test
    fun `doit additionner les points de toutes les cartes`() {
        val tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(effetScore = AjoutePoints(5)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(effetScore = AjoutePoints(5)), position = HAUTMILIEU),
            )
        )
        val joueur = Joueur(id = 1, tableau = tableau)

        scoreRepository.compteLeScore(listOf(joueur))

        assertThat(joueur.score).isEqualTo(10)
    }

    @Test
    fun `doit ignorer les cartes sans effet de score`() {
        val tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(effetScore = AjoutePoints(5)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(), position = HAUTMILIEU),
            )
        )
        val joueur = Joueur(id = 1, tableau = tableau)

        scoreRepository.compteLeScore(listOf(joueur))

        assertThat(joueur.score).isEqualTo(5)
    }

    @Test
    fun `doit calculer le score de chaque joueur independamment`() {
        val tableauJoueur1 = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(effetScore = AjoutePoints(5)), position = HAUTGAUCHE),
            )
        )
        val tableauJoueur2 = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(effetScore = AjoutePoints(5)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(effetScore = AjoutePoints(5)), position = HAUTMILIEU),
            )
        )
        val joueur1 = Joueur(id = 1, tableau = tableauJoueur1)
        val joueur2 = Joueur(id = 2, tableau = tableauJoueur2)

        scoreRepository.compteLeScore(listOf(joueur1, joueur2))

        assertThat(joueur1.score).isEqualTo(5)
        assertThat(joueur2.score).isEqualTo(10)
    }

    @Test
    fun `le score est zero si le tableau est vide`() {
        val joueur = Joueur(id = 1)

        scoreRepository.compteLeScore(listOf(joueur))

        assertThat(joueur.score).isEqualTo(0)
    }
}
