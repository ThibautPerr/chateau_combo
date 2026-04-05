package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetpoint.PointsParCarteAvecCoutExact
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTDROITE
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParCarteAvecCoutExactEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit donner des points par carte dont le cout correspond exactement`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(cout = 0), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(cout = 0), position = HAUTMILIEU),
                CartePositionee(carte = villageois(cout = 3), position = HAUTDROITE),
            )
        ))
        val carte = villageois(effetScore = PointsParCarteAvecCoutExact(points = 2, cout = 0))
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(4)
    }

    @Test
    fun `doit retourner zero si aucune carte n'a le cout exact`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(cout = 3), position = HAUTGAUCHE),
            )
        ))
        val carte = villageois(effetScore = PointsParCarteAvecCoutExact(points = 2, cout = 0))
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
