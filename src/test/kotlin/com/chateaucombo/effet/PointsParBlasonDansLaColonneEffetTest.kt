package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason.ERUDIT
import com.chateaucombo.deck.model.Blason.NOBLE
import com.chateaucombo.effet.model.PointsParBlasonDansLaColonne
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.BASMILIEU
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUDROITE
import com.chateaucombo.tableau.model.Position.MILIEUGAUCHE
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParBlasonDansLaColonneEffetTest : EffetTestBase() {
    @Test
    fun `doit compter les blasons dans la meme colonne`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT, ERUDIT)), position = HAUTMILIEU),
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = BASMILIEU),
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = MILIEUGAUCHE),
            )
        ))
        val carte = villageois(effetScore = PointsParBlasonDansLaColonne(points = 3, blason = ERUDIT))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(9)
    }

    @Test
    fun `ne doit pas compter les blasons des autres colonnes`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = MILIEUGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = MILIEUDROITE),
            )
        ))
        val carte = villageois(effetScore = PointsParBlasonDansLaColonne(points = 3, blason = ERUDIT))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }

    @Test
    fun `doit retourner zero si le blason est absent de la colonne`() {
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTMILIEU),
            )
        ))
        val carte = villageois(effetScore = PointsParBlasonDansLaColonne(points = 3, blason = ERUDIT))
        val context = ScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
