package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.Blason.ARTISAN
import com.chateaucombo.deck.carte.Blason.ERUDIT
import com.chateaucombo.deck.carte.Blason.MILITAIRE
import com.chateaucombo.deck.carte.Blason.NOBLE
import com.chateaucombo.deck.carte.effet.effetpoint.PointsParCarteAvecNbBlasonMinimum
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTDROITE
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParCarteAvecNbBlasonMinimumEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
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
        val context = EffetScoreContext(
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
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
