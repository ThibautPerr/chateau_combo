package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason.ARTISAN
import com.chateaucombo.deck.model.Blason.ERUDIT
import com.chateaucombo.deck.model.Blason.MILITAIRE
import com.chateaucombo.deck.model.Blason.NOBLE
import com.chateaucombo.deck.model.Blason.PAYSAN
import com.chateaucombo.effet.effetpoint.PointsParBlasonDistinctDansLaRangee
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.BASMILIEU
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUDROITE
import com.chateaucombo.tableau.model.Position.MILIEUGAUCHE
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParBlasonDistinctDansLaRangeeEffetTest : EffetTestBase() {
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
        val context = ScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

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
        val context = ScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

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
        val context = ScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

        assertThat(carte.effetScore.score(context)).isEqualTo(4)
    }
}
