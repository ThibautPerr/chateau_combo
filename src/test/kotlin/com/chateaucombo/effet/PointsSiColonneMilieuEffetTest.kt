package com.chateaucombo.effet

import com.chateaucombo.effet.model.PointsSiColonneMilieu
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUGAUCHE
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsSiColonneMilieuEffetTest : EffetTestBase() {
    @Test
    fun `doit ajouter les points si la carte est dans la colonne milieu`() {
        val carte = villageois(effetScore = PointsSiColonneMilieu(points = 6))
        val context = ScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(6)
    }

    @Test
    fun `doit ajouter les points meme si la carte n'est pas au centre du rang`() {
        val carte = villageois(effetScore = PointsSiColonneMilieu(points = 6))
        val context = ScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = HAUTMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(6)
    }

    @Test
    fun `ne doit pas ajouter de points si la carte n'est pas dans la colonne milieu`() {
        val carte = villageois(effetScore = PointsSiColonneMilieu(points = 6))
        val context = ScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUGAUCHE)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
