package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetpoint.PointsSiCoin
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class PointsSiCoinEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @ParameterizedTest
    @EnumSource(names = ["HAUTGAUCHE", "HAUTDROITE", "BASGAUCHE", "BASDROITE"])
    fun `doit ajouter les points si la carte est en position de coin`(position: Position) {
        val carte = villageois(effetScore = PointsSiCoin(points = 4))
        val context = EffetScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = position)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(4)
    }

    @ParameterizedTest
    @EnumSource(names = ["HAUTMILIEU", "MILIEUGAUCHE", "MILIEUMILIEU", "MILIEUDROITE", "BASMILIEU"])
    fun `ne doit pas ajouter de points si la carte n'est pas en coin`(position: Position) {
        val carte = villageois(effetScore = PointsSiCoin(points = 4))
        val context = EffetScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = position)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
