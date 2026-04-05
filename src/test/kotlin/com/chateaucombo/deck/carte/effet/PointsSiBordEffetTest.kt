package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetpoint.PointsSiBord
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class PointsSiBordEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @ParameterizedTest
    @EnumSource(names = ["HAUTMILIEU", "MILIEUGAUCHE", "MILIEUDROITE", "BASMILIEU"])
    fun `doit ajouter les points si la carte est en position de bord`(position: Position) {
        val carte = villageois(effetScore = PointsSiBord(points = 3))
        val context = EffetScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = position)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(3)
    }

    @ParameterizedTest
    @EnumSource(names = ["HAUTGAUCHE", "HAUTDROITE", "MILIEUMILIEU", "BASGAUCHE", "BASDROITE"])
    fun `ne doit pas ajouter de points si la carte est au centre ou en coin`(position: Position) {
        val carte = villageois(effetScore = PointsSiBord(points = 3))
        val context = EffetScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = position)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
