package com.chateaucombo.score

import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.effet.model.AjoutePoints
import com.chateaucombo.effet.model.BourseScore
import com.chateaucombo.effet.model.EffetScore
import com.chateaucombo.effet.model.EffetScoreVide
import com.chateaucombo.effet.model.Effets
import com.chateaucombo.effet.model.PointsParOrDepose
import com.chateaucombo.effet.model.PointsSiRangSuperieur
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ScoreTest {
    private fun villageois(effetScore: EffetScore = EffetScoreVide, bourse: BourseScore? = null) =
        Villageois(cout = 0, nom = "carte", blasons = emptyList(), effets = Effets(), effetScore = effetScore, bourse = bourse)

    @Nested
    inner class AjoutePointsEffet {
        @ParameterizedTest
        @ValueSource(ints = [1, 5, 10])
        fun `doit retourner le nombre de points fixes`(points: Int) {
            val carte = villageois(effetScore = AjoutePoints(points))
            val context = ScoreContext(joueurActuel = Joueur(id = 1), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            val score = AjoutePoints(points).score(context)

            assertThat(score).isEqualTo(points)
        }
    }

    @Nested
    inner class EffetScoreVideEffet {
        @Test
        fun `doit retourner zero points`() {
            val carte = villageois()
            val context = ScoreContext(joueurActuel = Joueur(id = 1), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            val score = EffetScoreVide.score(context)

            assertThat(score).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsParOrDeposeEffet {
        @Test
        fun `doit retourner le total d'or depose dans les bourses`() {
            val bourse1 = BourseScore(taille = 5).also { it.orDepose = 3 }
            val bourse2 = BourseScore(taille = 8).also { it.orDepose = 6 }
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(bourse = bourse1), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(bourse = bourse2), position = HAUTMILIEU),
                )
            ))
            val carte = villageois(effetScore = PointsParOrDepose())
            val context = ScoreContext(joueurActuel = joueur, cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            val score = PointsParOrDepose().score(context)

            assertThat(score).isEqualTo(9)
        }

        @Test
        fun `doit retourner zero si aucune bourse sur le tableau`() {
            val carte = villageois(effetScore = PointsParOrDepose())
            val context = ScoreContext(joueurActuel = Joueur(id = 1), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            val score = PointsParOrDepose().score(context)

            assertThat(score).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsSiRangSuperieurEffet {
        @Test
        fun `doit retourner les points si la carte est dans le rang superieur`() {
            val carte = villageois(effetScore = PointsSiRangSuperieur(points = 5))
            val context = ScoreContext(joueurActuel = Joueur(id = 1), cartePositionee = CartePositionee(carte = carte, position = HAUTGAUCHE))

            val score = PointsSiRangSuperieur(points = 5).score(context)

            assertThat(score).isEqualTo(5)
        }

        @Test
        fun `doit retourner zero si la carte n'est pas dans le rang superieur`() {
            val carte = villageois(effetScore = PointsSiRangSuperieur(points = 5))
            val context = ScoreContext(joueurActuel = Joueur(id = 1), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            val score = PointsSiRangSuperieur(points = 5).score(context)

            assertThat(score).isEqualTo(0)
        }
    }

}
