package com.chateaucombo.effet

import com.chateaucombo.effet.model.PointsSiBord
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class PointsSiBordEffetTest : EffetTestBase() {
    @ParameterizedTest
    @EnumSource(names = ["HAUTMILIEU", "MILIEUGAUCHE", "MILIEUDROITE", "BASMILIEU"])
    fun `doit ajouter les points si la carte est en position de bord`(position: Position) {
        val carte = villageois(effetScore = PointsSiBord(points = 3))
        val context = ScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = position)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(3)
    }

    @ParameterizedTest
    @EnumSource(names = ["HAUTGAUCHE", "HAUTDROITE", "MILIEUMILIEU", "BASGAUCHE", "BASDROITE"])
    fun `ne doit pas ajouter de points si la carte est au centre ou en coin`(position: Position) {
        val carte = villageois(effetScore = PointsSiBord(points = 3))
        val context = ScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = position)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
