package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetpoint.PointsSiRangMilieu
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUGAUCHE
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsSiRangMilieuEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit ajouter les points si la carte est dans le rang milieu vertical`() {
        val carte = villageois(effetScore = PointsSiRangMilieu(points = 5))
        val context = EffetScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(5)
    }

    @Test
    fun `doit ajouter les points meme si la carte n'est pas au centre`() {
        val carte = villageois(effetScore = PointsSiRangMilieu(points = 5))
        val context = EffetScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUGAUCHE)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(5)
    }

    @Test
    fun `ne doit pas ajouter de points si la carte n'est pas dans le rang milieu vertical`() {
        val carte = villageois(effetScore = PointsSiRangMilieu(points = 5))
        val context = EffetScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = HAUTMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
