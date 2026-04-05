package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.Blason.ARTISAN
import com.chateaucombo.deck.carte.Blason.ERUDIT
import com.chateaucombo.deck.carte.Blason.MILITAIRE
import com.chateaucombo.deck.carte.Blason.NOBLE
import com.chateaucombo.deck.carte.Blason.PAYSAN
import com.chateaucombo.deck.carte.Blason.RELIGIEUX
import com.chateaucombo.deck.carte.effet.effetpoint.PointsParBlasonManquant
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParBlasonManquantEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit donner des points par blason absent du tableau`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(NOBLE, MILITAIRE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(RELIGIEUX)), position = HAUTMILIEU),
            )
        ))
        val carte = chatelain(effetScore = PointsParBlasonManquant(points = 6))
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        // NOBLE, MILITAIRE, RELIGIEUX présents — ERUDIT, ARTISAN, PAYSAN absents → 3 * 6 = 18
        assertThat(carte.effetScore.score(context)).isEqualTo(18)
    }

    @Test
    fun `doit retourner zero si tous les blasons sont presents`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(NOBLE, MILITAIRE, RELIGIEUX)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT, ARTISAN, PAYSAN)), position = HAUTMILIEU),
            )
        ))
        val carte = chatelain(effetScore = PointsParBlasonManquant(points = 6))
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }

    @Test
    fun `ne doit pas compter les doublons comme blasons supplementaires`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(NOBLE, NOBLE)), position = HAUTGAUCHE),
            )
        ))
        val carte = chatelain(effetScore = PointsParBlasonManquant(points = 6))
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        // Seul NOBLE présent → 5 blasons manquants → 5 * 6 = 30
        assertThat(carte.effetScore.score(context)).isEqualTo(30)
    }
}
