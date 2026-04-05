package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutChatelain
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutVillageois
import com.chateaucombo.deck.carte.effet.effetpoint.PointsParCarteAvecReductionDeCout
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTDROITE
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParCarteAvecReductionDeCoutEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
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
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(8)
    }

    @Test
    fun `doit compter une carte avec les deux reductions`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = chatelain(effets = Effets(effetsPassifs = listOf(
                    ReduceCoutChatelain(),
                    ReduceCoutVillageois()
                ))), position = HAUTGAUCHE),
            )
        ))
        val carte = villageois(effetScore = PointsParCarteAvecReductionDeCout(points = 4))
        val context = EffetScoreContext(
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
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
