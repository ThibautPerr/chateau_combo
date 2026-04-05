package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.effet.effetpoint.PointsSiCarteVersoPresente
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsSiCarteVersoPresenteEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
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
        val context = EffetScoreContext(
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
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
