package com.chateaucombo.effet

import com.chateaucombo.effet.model.Effets
import com.chateaucombo.effet.model.PointsParCarteAvecReductionDeCout
import com.chateaucombo.effet.model.ReduceCoutChatelain
import com.chateaucombo.effet.model.ReduceCoutVillageois
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParCarteAvecReductionDeCoutEffetTest : EffetTestBase() {
    @Test
    fun `doit donner des points par carte avec reduction de cout chatelain ou villageois`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = chatelain(effets = Effets(effetsPassifs = listOf(ReduceCoutChatelain()))), position = HAUTGAUCHE),
                CartePositionee(carte = chatelain(effets = Effets(effetsPassifs = listOf(ReduceCoutVillageois()))), position = HAUTMILIEU),
                CartePositionee(carte = villageois(), position = HAUTDROITE),
            )
        ))
        val carte = villageois(effetScore = PointsParCarteAvecReductionDeCout(points = 4))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(8)
    }

    @Test
    fun `doit compter une carte avec les deux reductions`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = chatelain(effets = Effets(effetsPassifs = listOf(ReduceCoutChatelain(), ReduceCoutVillageois()))), position = HAUTGAUCHE),
            )
        ))
        val carte = villageois(effetScore = PointsParCarteAvecReductionDeCout(points = 4))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(4)
    }

    @Test
    fun `doit retourner zero si aucune carte n'a de reduction de cout`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(), position = HAUTGAUCHE),
            )
        ))
        val carte = villageois(effetScore = PointsParCarteAvecReductionDeCout(points = 4))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
