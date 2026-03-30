package com.chateaucombo.effet

import com.chateaucombo.deck.model.CarteVerso
import com.chateaucombo.effet.model.PointsSiCarteVersoPresente
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsSiCarteVersoPresenteEffetTest : EffetTestBase() {
    @Test
    fun `doit ajouter les points si au moins une carte verso est dans le tableau`() {
        val carteOriginale = villageois()
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = CarteVerso(carteOriginale = carteOriginale), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(), position = HAUTMILIEU),
            )
        ))
        val carte = villageois(effetScore = PointsSiCarteVersoPresente(points = 8))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(8)
    }

    @Test
    fun `ne doit pas ajouter de points si aucune carte verso n'est dans le tableau`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(), position = HAUTGAUCHE),
            )
        ))
        val carte = villageois(effetScore = PointsSiCarteVersoPresente(points = 8))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
