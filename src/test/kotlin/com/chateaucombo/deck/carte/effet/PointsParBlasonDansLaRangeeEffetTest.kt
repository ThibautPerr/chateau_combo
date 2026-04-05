package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.Blason.ERUDIT
import com.chateaucombo.deck.carte.Blason.NOBLE
import com.chateaucombo.deck.carte.effet.effetpoint.PointsParBlasonDansLaRangee
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.BASMILIEU
import com.chateaucombo.tableau.Position.HAUTDROITE
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUGAUCHE
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParBlasonDansLaRangeeEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit compter les blasons dans la meme rangee`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(NOBLE, NOBLE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTDROITE),
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = MILIEUGAUCHE),
            )
        ))
        val carte = villageois(effetScore = PointsParBlasonDansLaRangee(points = 3, blason = NOBLE))
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = HAUTMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(9)
    }

    @Test
    fun `ne doit pas compter les blasons des autres rangees`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = MILIEUGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = BASMILIEU),
            )
        ))
        val carte = villageois(effetScore = PointsParBlasonDansLaRangee(points = 3, blason = NOBLE))
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = HAUTMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }

    @Test
    fun `doit retourner zero si le blason est absent de la rangee`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
            )
        ))
        val carte = villageois(effetScore = PointsParBlasonDansLaRangee(points = 3, blason = NOBLE))
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = HAUTMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
