package com.chateaucombo.effet

import com.chateaucombo.effet.model.PointsParTripleVillageois
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PointsParTripleVillageoisEffetTest : EffetTestBase() {
    @ParameterizedTest
    @CsvSource("0, 0", "1, 0", "2, 0", "3, 7", "5, 7", "6, 14", "9, 21")
    fun `doit donner des points par triple de villageois`(nbVillageois: Int, pointsAttendus: Int) {
        val cartesPositionees = Position.entries.take(nbVillageois)
            .map { CartePositionee(carte = villageois(), position = it) }
            .toMutableList()
        val joueur = Joueur(id = 1, tableau = Tableau(cartesPositionees = cartesPositionees))
        val carte = villageois(effetScore = PointsParTripleVillageois(points = 7))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(pointsAttendus)
    }
}
