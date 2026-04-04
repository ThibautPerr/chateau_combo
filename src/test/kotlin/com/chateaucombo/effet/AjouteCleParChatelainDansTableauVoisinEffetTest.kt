package com.chateaucombo.effet

import com.chateaucombo.effet.effetplacement.AjouteCleParChatelainDansTableauVoisin
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteCleParChatelainDansTableauVoisinEffetTest : EffetTestBase() {
    @Test
    fun `doit ajouter autant de cles que de chatelains dans le tableau du voisin`() {
        val cleInitiale = 2
        val tableauVoisin = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
                CartePositionee(carte = chatelain(), position = HAUTMILIEU),
                CartePositionee(carte = chatelain(), position = HAUTDROITE),
            )
        )
        val voisin = Joueur(id = 0, tableau = tableauVoisin)
        val joueurActuel = Joueur(id = 1, cle = cleInitiale)
        val carte = villageois(effets = Effets(effets = listOf(AjouteCleParChatelainDansTableauVoisin())))
        val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisin, joueurActuel), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueurActuel.cle).isEqualTo(cleInitiale + 3)
    }

    @Test
    fun `ne doit pas ajouter de cles si le voisin n'a aucun chatelain`() {
        val cleInitiale = 2
        val tableauVoisin = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(), position = HAUTGAUCHE),
            )
        )
        val voisin = Joueur(id = 0, tableau = tableauVoisin)
        val joueurActuel = Joueur(id = 1, cle = cleInitiale)
        val carte = villageois(effets = Effets(effets = listOf(AjouteCleParChatelainDansTableauVoisin())))
        val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisin, joueurActuel), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueurActuel.cle).isEqualTo(cleInitiale)
    }

    @Test
    fun `ne doit pas ajouter de cles s'il n'y a pas de voisin`() {
        val cleInitiale = 2
        val joueurActuel = Joueur(id = 1, cle = cleInitiale)
        val carte = villageois(effets = Effets(effets = listOf(AjouteCleParChatelainDansTableauVoisin())))
        val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueurActuel.cle).isEqualTo(cleInitiale)
    }

    @Test
    fun `doit choisir le voisin qui donne le plus de cles`() {
        val cleInitiale = 2
        val tableauPauvre = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
            )
        )
        val tableauRiche = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
                CartePositionee(carte = chatelain(), position = HAUTMILIEU),
                CartePositionee(carte = chatelain(), position = HAUTDROITE),
            )
        )
        val joueurActuel = Joueur(id = 1, cle = cleInitiale)
        val voisinPauvre = Joueur(id = 0, tableau = tableauPauvre)
        val voisinRiche = Joueur(id = 2, tableau = tableauRiche)
        val carte = villageois(effets = Effets(effets = listOf(AjouteCleParChatelainDansTableauVoisin())))
        val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisinPauvre, joueurActuel, voisinRiche), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueurActuel.cle).isEqualTo(cleInitiale + 3)
    }

    @Test
    fun `doit fonctionner en position circulaire (premier et dernier joueur sont voisins)`() {
        val cleInitiale = 2
        val tableauVoisin = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
            )
        )
        val premierJoueur = Joueur(id = 0, tableau = tableauVoisin)
        val dernierJoueur = Joueur(id = 2, cle = cleInitiale)
        val autreJoueur = Joueur(id = 1)
        val carte = villageois(effets = Effets(effets = listOf(AjouteCleParChatelainDansTableauVoisin())))
        val context = EffetContext(joueurActuel = dernierJoueur, joueurs = listOf(premierJoueur, autreJoueur, dernierJoueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(dernierJoueur.cle).isEqualTo(cleInitiale + 1)
    }
}
