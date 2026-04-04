package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason.MILITAIRE
import com.chateaucombo.deck.model.Blason.NOBLE
import com.chateaucombo.effet.effetplacement.AjouteCleParBlasonDansTableauVoisin
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteCleParBlasonDansTableauVoisinEffetTest : EffetTestBase() {
    @Test
    fun `doit ajouter autant de cles que d'occurrences du blason dans le tableau du voisin`() {
        val cleInitiale = 2
        val tableauVoisin = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, NOBLE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTMILIEU),
            )
        )
        val joueurActuel = Joueur(id = 0, cle = cleInitiale)
        val voisin = Joueur(id = 1, tableau = tableauVoisin)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDansTableauVoisin(MILITAIRE))))
        val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel, voisin), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueurActuel.cle).isEqualTo(cleInitiale + 2)
    }

    @Test
    fun `ne doit pas modifier le tableau du voisin`() {
        val cleVoisin = 5
        val tableauVoisin = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTGAUCHE),
            )
        )
        val joueurActuel = Joueur(id = 0)
        val voisin = Joueur(id = 1, cle = cleVoisin, tableau = tableauVoisin)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDansTableauVoisin(MILITAIRE))))
        val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel, voisin), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(voisin.cle).isEqualTo(cleVoisin)
    }

    @Test
    fun `ne doit pas ajouter de cles si le joueur actuel est seul`() {
        val cleInitiale = 2
        val joueurActuel = Joueur(id = 0, cle = cleInitiale)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDansTableauVoisin(MILITAIRE))))
        val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueurActuel.cle).isEqualTo(cleInitiale)
    }

    @Test
    fun `doit choisir le voisin qui donne le plus de cles`() {
        val cleInitiale = 2
        val tableauPauvre = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTGAUCHE),
            )
        )
        val tableauRiche = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTMILIEU),
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTDROITE),
            )
        )
        val joueurActuel = Joueur(id = 1, cle = cleInitiale)
        val voisinPauvre = Joueur(id = 0, tableau = tableauPauvre)
        val voisinRiche = Joueur(id = 2, tableau = tableauRiche)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDansTableauVoisin(MILITAIRE))))
        val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisinPauvre, joueurActuel, voisinRiche), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueurActuel.cle).isEqualTo(cleInitiale + 3)
    }
}
