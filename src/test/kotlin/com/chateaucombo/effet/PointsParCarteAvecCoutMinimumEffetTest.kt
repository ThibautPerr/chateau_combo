package com.chateaucombo.effet

import com.chateaucombo.effet.model.PointsParCarteAvecCoutMinimum
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParCarteAvecCoutMinimumEffetTest : EffetTestBase() {
    @Test
    fun `doit donner des points par carte dont le cout est superieur ou egal au minimum`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(cout = 5), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(cout = 6), position = HAUTMILIEU),
                CartePositionee(carte = villageois(cout = 4), position = HAUTDROITE),
            )
        ))
        val carte = villageois(effetScore = PointsParCarteAvecCoutMinimum(points = 5, coutMinimum = 5))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(10)
    }

    @Test
    fun `doit retourner zero si aucune carte n'atteint le cout minimum`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(cout = 3), position = HAUTGAUCHE),
            )
        ))
        val carte = villageois(effetScore = PointsParCarteAvecCoutMinimum(points = 5, coutMinimum = 5))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
