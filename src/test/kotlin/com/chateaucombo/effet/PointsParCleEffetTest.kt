package com.chateaucombo.effet

import com.chateaucombo.effet.effetpoint.PointsParCle
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PointsParCleEffetTest : EffetTestBase() {
    @ParameterizedTest
    @ValueSource(ints = [0, 1, 5, 13])
    fun `doit donner autant de points que de cles`(cle: Int) {
        val joueur = Joueur(id = 1, cle = cle)
        val carte = villageois(effetScore = PointsParCle())
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(cle)
    }
}
