package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetpoint.PointsSiColonneDroite
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTDROITE
import com.chateaucombo.tableau.Position.MILIEUDROITE
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsSiColonneDroiteEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit ajouter les points si la carte est dans la colonne droite`() {
        val carte = villageois(effetScore = PointsSiColonneDroite(points = 5))
        val context = EffetScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUDROITE)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(5)
    }

    @Test
    fun `doit ajouter les points meme si la carte n'est pas au centre du rang`() {
        val carte = villageois(effetScore = PointsSiColonneDroite(points = 5))
        val context = EffetScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = HAUTDROITE)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(5)
    }

    @Test
    fun `ne doit pas ajouter de points si la carte n'est pas dans la colonne droite`() {
        val carte = villageois(effetScore = PointsSiColonneDroite(points = 5))
        val context = EffetScoreContext(
            joueurActuel = Joueur(id = 1),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
