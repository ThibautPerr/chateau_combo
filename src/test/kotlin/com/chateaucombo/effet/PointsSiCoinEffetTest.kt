package com.chateaucombo.effet

import com.chateaucombo.effet.model.PointsSiCoin
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class PointsSiCoinEffetTest : EffetTestBase() {
    @ParameterizedTest
    @EnumSource(names = ["HAUTGAUCHE", "HAUTDROITE", "BASGAUCHE", "BASDROITE"])
    fun `doit ajouter les points si la carte est en position de coin`(position: Position) {
        val carte = villageois(effetScore = PointsSiCoin(points = 4))
        val context = ScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = position)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(4)
    }

    @ParameterizedTest
    @EnumSource(names = ["HAUTMILIEU", "MILIEUGAUCHE", "MILIEUMILIEU", "MILIEUDROITE", "BASMILIEU"])
    fun `ne doit pas ajouter de points si la carte n'est pas en coin`(position: Position) {
        val carte = villageois(effetScore = PointsSiCoin(points = 4))
        val context = ScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = position)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
