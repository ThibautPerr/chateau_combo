package com.chateaucombo.score

import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.effet.model.AjoutePoints
import com.chateaucombo.effet.model.BourseScore
import com.chateaucombo.effet.model.EffetScoreVide
import com.chateaucombo.effet.model.Effets
import com.chateaucombo.effet.model.PointsParOrDepose
import com.chateaucombo.effet.model.PointsSiRangSuperieur
import com.chateaucombo.tableau.model.Position.BASGAUCHE
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
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

    @Nested
    inner class BourseScoreEffet {
        @Test
        fun `doit mettre les ors dans la bourse et valoir deux points par or quand or inferieur a la taille`() {
            val carte = villageois(effetScore = BourseScore(taille = 5))
            val tableau = Tableau(cartesPositionees = mutableListOf(CartePositionee(carte = carte, position = HAUTGAUCHE)))
            val joueur = Joueur(id = 1, or = 3, tableau = tableau)

            scoreRepository.compteLeScore(listOf(joueur))

            assertThat(joueur.score).isEqualTo(6)
        }

        @Test
        fun `doit remplir la bourse au maximum quand or superieur a la taille`() {
            val carte = villageois(effetScore = BourseScore(taille = 5))
            val tableau = Tableau(cartesPositionees = mutableListOf(CartePositionee(carte = carte, position = HAUTGAUCHE)))
            val joueur = Joueur(id = 1, or = 10, tableau = tableau)

            scoreRepository.compteLeScore(listOf(joueur))

            assertThat(joueur.score).isEqualTo(10)
        }

        @Test
        fun `doit additionner la capacite de plusieurs bourses`() {
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(effetScore = BourseScore(taille = 3)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(effetScore = BourseScore(taille = 4)), position = HAUTMILIEU),
                )
            )
            val joueur = Joueur(id = 1, or = 5, tableau = tableau)

            scoreRepository.compteLeScore(listOf(joueur))

            assertThat(joueur.score).isEqualTo(10)
        }

        @Test
        fun `doit remplir toutes les bourses quand or depasse la capacite totale`() {
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(effetScore = BourseScore(taille = 3)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(effetScore = BourseScore(taille = 4)), position = HAUTMILIEU),
                )
            )
            val joueur = Joueur(id = 1, or = 20, tableau = tableau)

            scoreRepository.compteLeScore(listOf(joueur))

            assertThat(joueur.score).isEqualTo(14)
        }

        @Test
        fun `doit combiner bourse et effets de score par carte`() {
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(effetScore = BourseScore(taille = 5)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(effetScore = AjoutePoints(3)), position = HAUTMILIEU),
                )
            )
            val joueur = Joueur(id = 1, or = 4, tableau = tableau)

            scoreRepository.compteLeScore(listOf(joueur))

            assertThat(joueur.score).isEqualTo(11)
        }

        @Test
        fun `doit retourner zero si pas de bourse dans le tableau`() {
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(), position = HAUTGAUCHE),
                )
            )
            val joueur = Joueur(id = 1, or = 10, tableau = tableau)

            scoreRepository.compteLeScore(listOf(joueur))

            assertThat(joueur.score).isEqualTo(0)
        }

        @Test
        fun `doit combiner l'or des bourses pre-remplies avec l'or du joueur`() {
            val bourse = BourseScore(taille = 5).also { it.orDepose = 3 }
            val tableau = Tableau(cartesPositionees = mutableListOf(CartePositionee(carte = villageois(effetScore = bourse), position = HAUTGAUCHE)))
            val joueur = Joueur(id = 1, or = 2, tableau = tableau)

            scoreRepository.compteLeScore(listOf(joueur))

            assertThat(joueur.score).isEqualTo(10)
        }

        @Test
        fun `une bourse deja pleine vaut sa taille fois deux meme si le joueur a de l'or`() {
            val bourse = BourseScore(taille = 4).also { it.orDepose = 4 }
            val tableau = Tableau(cartesPositionees = mutableListOf(CartePositionee(carte = villageois(effetScore = bourse), position = HAUTGAUCHE)))
            val joueur = Joueur(id = 1, or = 10, tableau = tableau)

            scoreRepository.compteLeScore(listOf(joueur))

            assertThat(joueur.score).isEqualTo(8)
        }
    }

    @Nested
    inner class PointsParOrDeposeEffet {
        @Test
        fun `doit compter l'or total dans les bourses incluant l'or du joueur verse en fin de partie`() {
            val bourse = BourseScore(taille = 5)
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(effetScore = bourse), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(effetScore = PointsParOrDepose()), position = HAUTMILIEU),
                )
            )
            val joueur = Joueur(id = 1, or = 3, tableau = tableau)

            scoreRepository.compteLeScore(listOf(joueur))

            assertThat(joueur.score).isEqualTo(6 + 3)
        }
    }

    @Nested
    inner class PointsSiRangSuperieurEffet {
        @Test
        fun `doit rapporter des points si la carte est dans le rang superieur`() {
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(effetScore = PointsSiRangSuperieur(points = 5)), position = HAUTGAUCHE),
                )
            )
            val joueur = Joueur(id = 1, tableau = tableau)

            scoreRepository.compteLeScore(listOf(joueur))

            assertThat(joueur.score).isEqualTo(5)
        }

        @Test
        fun `ne doit pas rapporter de points si la carte n'est pas dans le rang superieur`() {
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(effetScore = PointsSiRangSuperieur(points = 5)), position = MILIEUMILIEU),
                )
            )
            val joueur = Joueur(id = 1, tableau = tableau)

            scoreRepository.compteLeScore(listOf(joueur))

            assertThat(joueur.score).isEqualTo(0)
        }
    }
}
