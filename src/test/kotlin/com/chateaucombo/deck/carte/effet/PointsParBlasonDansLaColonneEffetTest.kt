package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.Blason.ERUDIT
import com.chateaucombo.deck.carte.Blason.NOBLE
import com.chateaucombo.deck.carte.effet.effetpoint.PointsParBlasonDansLaColonne
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.BASMILIEU
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUDROITE
import com.chateaucombo.tableau.Position.MILIEUGAUCHE
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointsParBlasonDansLaColonneEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
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
        val context = EffetScoreContext(
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
        val context = EffetScoreContext(
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
        val context = EffetScoreContext(
            joueurActuel = joueur,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
        )

        assertThat(carte.effetScore.score(context)).isEqualTo(0)
    }
}
