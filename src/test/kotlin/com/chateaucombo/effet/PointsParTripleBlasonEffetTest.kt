package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason.ERUDIT
import com.chateaucombo.deck.model.Blason.MILITAIRE
import com.chateaucombo.deck.model.Blason.NOBLE
import com.chateaucombo.effet.model.PointsParTripleBlason
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParTripleBlasonEffetTest : EffetTestBase() {
    @Test
    fun `doit donner des points par triple d'un meme blason`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(NOBLE, NOBLE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTMILIEU),
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, MILITAIRE, MILITAIRE)), position = HAUTDROITE),
            )
        ))
        val carte = villageois(effetScore = PointsParTripleBlason(points = 6))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(12)
    }

    @Test
    fun `doit compter les triples independamment par type de blason`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(NOBLE, NOBLE, NOBLE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT, ERUDIT, ERUDIT)), position = HAUTMILIEU),
            )
        ))
        val carte = villageois(effetScore = PointsParTripleBlason(points = 6))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(12)
    }

    @Test
    fun `doit retourner zero si aucun triple n'est present`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(NOBLE, NOBLE)), position = HAUTGAUCHE),
            )
        ))
        val carte = villageois(effetScore = PointsParTripleBlason(points = 6))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
