package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason.ERUDIT
import com.chateaucombo.deck.model.Blason.MILITAIRE
import com.chateaucombo.deck.model.Blason.NOBLE
import com.chateaucombo.deck.model.Blason.PAYSAN
import com.chateaucombo.deck.model.Blason.RELIGIEUX
import com.chateaucombo.effet.effetpoint.PointsParGroupeDeBlasons
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParGroupeDeBlasonsEffetTest : EffetTestBase() {
    @Test
    fun `doit donner des points par groupe complet de blasons (triple)`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(RELIGIEUX, MILITAIRE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(PAYSAN, RELIGIEUX)), position = HAUTMILIEU),
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, PAYSAN)), position = HAUTDROITE),
            )
        ))
        val carte = villageois(effetScore = PointsParGroupeDeBlasons(
            points = 7,
            blasons = listOf(RELIGIEUX, MILITAIRE, PAYSAN)
        )
        )
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(14)
    }

    @Test
    fun `doit donner des points par groupe complet de blasons (paire)`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT, ERUDIT)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(PAYSAN)), position = HAUTMILIEU),
            )
        ))
        val carte = villageois(effetScore = PointsParGroupeDeBlasons(points = 4, blasons = listOf(ERUDIT, PAYSAN)))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(4)
    }

    @Test
    fun `doit etre limite par le blason le moins present`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(NOBLE, NOBLE, NOBLE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTMILIEU),
            )
        ))
        val carte = villageois(effetScore = PointsParGroupeDeBlasons(points = 4, blasons = listOf(NOBLE, MILITAIRE)))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(4)
    }

    @Test
    fun `doit retourner zero si un blason requis est absent`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT, ERUDIT)), position = HAUTGAUCHE),
            )
        ))
        val carte = villageois(effetScore = PointsParGroupeDeBlasons(points = 4, blasons = listOf(ERUDIT, PAYSAN)))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
