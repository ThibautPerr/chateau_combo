package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason.ERUDIT
import com.chateaucombo.deck.model.Blason.NOBLE
import com.chateaucombo.effet.model.AjouteOrParBlasonDansTableauVoisin
import com.chateaucombo.effet.model.EffetContext
import com.chateaucombo.effet.model.Effets
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteOrParBlasonDansTableauVoisinEffetTest : EffetTestBase() {
    @Test
    fun `doit ajouter autant d'or que de blasons du type indique dans le tableau du voisin`() {
        val orInitial = 2
        val tableauVoisin = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT, NOBLE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTMILIEU),
            )
        )
        val joueurActuel = Joueur(id = 0, or = orInitial)
        val voisin = Joueur(id = 1, tableau = tableauVoisin)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDansTableauVoisin(ERUDIT))))
        val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel, voisin), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueurActuel.or).isEqualTo(orInitial + 2)
    }

    @Test
    fun `ne doit pas modifier le tableau du voisin`() {
        val orVoisin = 5
        val tableauVoisin = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
            )
        )
        val joueurActuel = Joueur(id = 0)
        val voisin = Joueur(id = 1, or = orVoisin, tableau = tableauVoisin)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDansTableauVoisin(ERUDIT))))
        val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel, voisin), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(voisin.or).isEqualTo(orVoisin)
    }

    @Test
    fun `ne doit pas ajouter d'or si le joueur actuel est seul`() {
        val orInitial = 2
        val joueurActuel = Joueur(id = 0, or = orInitial)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDansTableauVoisin(ERUDIT))))
        val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueurActuel.or).isEqualTo(orInitial)
    }

    @Test
    fun `le premier et le dernier joueur sont voisins`() {
        val orInitial = 2
        val tableauVoisin = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
            )
        )
        val premierJoueur = Joueur(id = 0, tableau = tableauVoisin)
        val dernierJoueur = Joueur(id = 2, or = orInitial)
        val autreJoueur = Joueur(id = 1)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDansTableauVoisin(ERUDIT))))
        val context = EffetContext(joueurActuel = dernierJoueur, joueurs = listOf(premierJoueur, autreJoueur, dernierJoueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(dernierJoueur.or).isEqualTo(orInitial + 1)
    }

    @Test
    fun `doit choisir le voisin qui donne le plus d'or`() {
        val orInitial = 2
        val tableauPauvre = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
            )
        )
        val tableauRiche = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTMILIEU),
                CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTDROITE),
            )
        )
        val joueurActuel = Joueur(id = 1, or = orInitial)
        val voisinPauvre = Joueur(id = 0, tableau = tableauPauvre)
        val voisinRiche = Joueur(id = 2, tableau = tableauRiche)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDansTableauVoisin(ERUDIT))))
        val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisinPauvre, joueurActuel, voisinRiche), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueurActuel.or).isEqualTo(orInitial + 3)
    }
}
