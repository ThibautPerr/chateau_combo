package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason.ERUDIT
import com.chateaucombo.deck.model.Blason.MILITAIRE
import com.chateaucombo.deck.model.Blason.NOBLE
import com.chateaucombo.effet.effetpoint.PointsSiBlasonAbsent
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsSiBlasonAbsentEffetTest : EffetTestBase() {
    @Test
    fun `doit ajouter les points si aucune carte du tableau n'a le blason`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTMILIEU),
            )
        ))
        val carte = villageois(effetScore = PointsSiBlasonAbsent(points = 10, blason = MILITAIRE))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(10)
    }

    @Test
    fun `ne doit pas ajouter de points si une carte du tableau a le blason`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTGAUCHE),
            )
        ))
        val carte = villageois(effetScore = PointsSiBlasonAbsent(points = 10, blason = MILITAIRE))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }

    @Test
    fun `doit ajouter les points si le tableau est vide`() {
        val joueur = Joueur(id = 1)
        val carte = villageois(effetScore = PointsSiBlasonAbsent(points = 10, blason = MILITAIRE))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(10)
    }
}
