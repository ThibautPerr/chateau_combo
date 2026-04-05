package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetpoint.PointsParChatelain
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PointsParChatelainEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @ParameterizedTest
    @CsvSource("0, 0", "1, 2", "3, 6")
    fun `doit donner des points par chatelain sur le tableau`(nbChatelains: Int, pointsAttendus: Int) {
        val cartesPositionees = Position.entries.take(nbChatelains)
            .map { CartePositionee(carte = chatelain(), position = it) }
            .toMutableList()
        val joueur = Joueur(id = 1, tableau = Tableau(cartesPositionees = cartesPositionees))
        val carte = villageois(effetScore = PointsParChatelain(points = 2))
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(pointsAttendus)
    }

    @Test
    fun `ne doit pas compter les villageois`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(), position = HAUTMILIEU),
            )
        ))
        val carte = villageois(effetScore = PointsParChatelain(points = 2))
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(2)
    }
}
