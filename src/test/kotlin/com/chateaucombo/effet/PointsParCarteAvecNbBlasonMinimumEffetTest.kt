package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason.ARTISAN
import com.chateaucombo.deck.model.Blason.ERUDIT
import com.chateaucombo.deck.model.Blason.MILITAIRE
import com.chateaucombo.deck.model.Blason.NOBLE
import com.chateaucombo.effet.model.PointsParCarteAvecNbBlasonMinimum
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

class PointsParCarteAvecNbBlasonMinimumEffetTest : EffetTestBase() {
    @Test
    fun `doit donner des points par carte ayant au moins le nombre de blasons requis`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(ARTISAN, MILITAIRE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(NOBLE, NOBLE)), position = HAUTMILIEU),
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTDROITE),
            )
        ))
        val carte = villageois(effetScore = PointsParCarteAvecNbBlasonMinimum(points = 2, nbBlasonMinimum = 2))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(4)
    }

    @Test
    fun `doit retourner zero si aucune carte n'a suffisamment de blasons`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
            )
        ))
        val carte = villageois(effetScore = PointsParCarteAvecNbBlasonMinimum(points = 2, nbBlasonMinimum = 2))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
