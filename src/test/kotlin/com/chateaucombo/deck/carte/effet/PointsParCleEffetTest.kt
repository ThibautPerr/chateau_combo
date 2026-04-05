package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetpoint.PointsParCle
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PointsParCleEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @ParameterizedTest
    @ValueSource(ints = [0, 1, 5, 13])
    fun `doit donner autant de points que de cles`(cle: Int) {
        val joueur = Joueur(id = 1, cle = cle)
        val carte = villageois(effetScore = PointsParCle())
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(cle)
    }
}
