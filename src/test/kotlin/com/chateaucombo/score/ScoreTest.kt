package com.chateaucombo.score

import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.effet.model.AjoutePoints
import com.chateaucombo.effet.model.EffetScore
import com.chateaucombo.effet.model.EffetScoreVide
import com.chateaucombo.effet.model.Effets
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ScoreTest {
    private fun villageois(effetScore: EffetScore = EffetScoreVide) =
        Villageois(cout = 0, nom = "carte", blasons = emptyList(), effets = Effets(), effetScore = effetScore)

    @Nested
    inner class AjoutePointsEffet {
        @ParameterizedTest
        @ValueSource(ints = [1, 5, 10])
        fun `doit retourner le nombre de points fixes`(points: Int) {
            val joueur = Joueur(id = 1)
            val carte = villageois(effetScore = AjoutePoints(points))
            val context = ScoreContext(joueurActuel = joueur, carte = carte)

            val score = AjoutePoints(points).score(context)

            assertThat(score).isEqualTo(points)
        }
    }

    @Nested
    inner class EffetScoreVideEffet {
        @Test
        fun `doit retourner zero points`() {
            val joueur = Joueur(id = 1)
            val carte = villageois()
            val context = ScoreContext(joueurActuel = joueur, carte = carte)

            val score = EffetScoreVide.score(context)

            assertThat(score).isEqualTo(0)
        }
    }

}
