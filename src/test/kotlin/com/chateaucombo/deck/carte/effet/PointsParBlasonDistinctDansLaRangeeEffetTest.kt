package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.Blason.ARTISAN
import com.chateaucombo.deck.carte.Blason.ERUDIT
import com.chateaucombo.deck.carte.Blason.MILITAIRE
import com.chateaucombo.deck.carte.Blason.NOBLE
import com.chateaucombo.deck.carte.Blason.PAYSAN
import com.chateaucombo.deck.carte.effet.effetpoint.PointsParBlasonDistinctDansLaRangee
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.BASMILIEU
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUDROITE
import com.chateaucombo.tableau.Position.MILIEUGAUCHE
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParBlasonDistinctDansLaRangeeEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit compter les blasons distincts dans la meme rangee`() {
        val carte = villageois(blasons = listOf(MILITAIRE), effetScore = PointsParBlasonDistinctDansLaRangee(points = 2))
        val cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(NOBLE, ERUDIT)), position = MILIEUGAUCHE),
                cartePositionee,
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT, NOBLE)), position = MILIEUDROITE),
                CartePositionee(carte = villageois(blasons = listOf(PAYSAN)), position = HAUTMILIEU),
            )
        ))
        val context = EffetScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

        assertThat(carte.effetScore.score(context)).isEqualTo(6)
    }

    @Test
    fun `ne doit pas compter les blasons des autres rangees`() {
        val carte = villageois(effetScore = PointsParBlasonDistinctDansLaRangee(points = 2))
        val cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                cartePositionee,
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTMILIEU),
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = BASMILIEU),
            )
        ))
        val context = EffetScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }

    @Test
    fun `doit compter les blasons de la carte elle-meme`() {
        val carte = villageois(blasons = listOf(ARTISAN, PAYSAN), effetScore = PointsParBlasonDistinctDansLaRangee(
            points = 2
        )
        )
        val cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(cartePositionee)
        ))
        val context = EffetScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

        assertThat(carte.effetScore.score(context)).isEqualTo(4)
    }
}
