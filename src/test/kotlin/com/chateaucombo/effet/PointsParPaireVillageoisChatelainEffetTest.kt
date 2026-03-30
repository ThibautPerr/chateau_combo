package com.chateaucombo.effet

import com.chateaucombo.effet.model.PointsParPaireVillageoisChatelain
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUGAUCHE
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParPaireVillageoisChatelainEffetTest : EffetTestBase() {
    @Test
    fun `doit donner des points par paire villageois-chatelain complete`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(), position = HAUTMILIEU),
                CartePositionee(carte = chatelain(), position = HAUTDROITE),
                CartePositionee(carte = chatelain(), position = MILIEUGAUCHE),
            )
        ))
        val carte = chatelain(effetScore = PointsParPaireVillageoisChatelain(points = 3))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        // 2 villageois, 2 chatelains → min(2, 2) * 3 = 6
        assertThat(carte.effetScore.score(context)).isEqualTo(6)
    }

    @Test
    fun `doit etre limite par le type le moins present`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(), position = HAUTMILIEU),
                CartePositionee(carte = villageois(), position = HAUTDROITE),
                CartePositionee(carte = chatelain(), position = MILIEUGAUCHE),
            )
        ))
        val carte = chatelain(effetScore = PointsParPaireVillageoisChatelain(points = 3))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        // 3 villageois, 1 chatelain → min(3, 1) * 3 = 3
        assertThat(carte.effetScore.score(context)).isEqualTo(3)
    }

    @Test
    fun `doit retourner zero si aucun villageois`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
                CartePositionee(carte = chatelain(), position = HAUTMILIEU),
            )
        ))
        val carte = chatelain(effetScore = PointsParPaireVillageoisChatelain(points = 3))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
