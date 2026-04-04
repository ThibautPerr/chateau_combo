package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason.NOBLE
import com.chateaucombo.effet.effetpoint.PointsParBlasonDansLaRangeeEtLaColonne
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUGAUCHE
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUDROITE
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParBlasonDansLaRangeeEtLaColonneEffetTest : EffetTestBase() {
    @Test
    fun `doit compter les blasons dans la rangee et la colonne`() {
        val carte = villageois(effetScore = PointsParBlasonDansLaRangeeEtLaColonne(points = 2, blason = NOBLE))
        val cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                cartePositionee,
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = MILIEUGAUCHE),  // rangee
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTMILIEU),   // colonne
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTGAUCHE),   // ni rangee ni colonne
            )
        ))
        val context = ScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

        // rangee: MILIEUGAUCHE(1) + MILIEUMILIEU(0) = 1 ; colonne: HAUTMILIEU(1) + MILIEUMILIEU(0) = 1 → total 2 * 2 = 4
        assertThat(carte.effetScore.score(context)).isEqualTo(4)
    }

    @Test
    fun `ne doit pas double-compter la carte a l'intersection`() {
        val carte = villageois(blasons = listOf(NOBLE), effetScore = PointsParBlasonDansLaRangeeEtLaColonne(
            points = 2,
            blason = NOBLE
        )
        )
        val cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(cartePositionee)
        ))
        val context = ScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

        // La carte elle-même a 1 NOBLE, comptée une seule fois → 1 * 2 = 2
        assertThat(carte.effetScore.score(context)).isEqualTo(2)
    }

    @Test
    fun `ne doit pas compter les blasons hors de la rangee et la colonne`() {
        val carte = villageois(effetScore = PointsParBlasonDansLaRangeeEtLaColonne(points = 2, blason = NOBLE))
        val cartePositionee = CartePositionee(carte = carte, position = HAUTGAUCHE)
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                cartePositionee,
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = MILIEUDROITE), // ni rangee ni colonne
            )
        ))
        val context = ScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
