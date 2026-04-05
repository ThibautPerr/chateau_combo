package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetpoint.PointsSiRangInferieur
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.BASGAUCHE
import com.chateaucombo.tableau.Position.BASMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsSiRangInferieurEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit ajouter les points si la carte est dans le rang inferieur`() {
        val carte = villageois(effetScore = PointsSiRangInferieur(points = 7))
        val context = EffetScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = BASMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(7)
    }

    @Test
    fun `doit ajouter les points meme si la carte n'est pas au centre du rang`() {
        val carte = villageois(effetScore = PointsSiRangInferieur(points = 7))
        val context = EffetScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = BASGAUCHE)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(7)
    }

    @Test
    fun `ne doit pas ajouter de points si la carte n'est pas dans le rang inferieur`() {
        val carte = villageois(effetScore = PointsSiRangInferieur(points = 7))
        val context = EffetScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
