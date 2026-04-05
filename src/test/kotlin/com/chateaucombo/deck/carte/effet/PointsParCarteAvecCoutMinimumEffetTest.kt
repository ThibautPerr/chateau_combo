package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetpoint.PointsParCarteAvecCoutMinimum
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTDROITE
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParCarteAvecCoutMinimumEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
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
        val context = EffetScoreContext(
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
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
