package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCleParCarteBourse
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTDROITE
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteCleParCarteBourseEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit ajouter une cle par carte avec bourse dans le tableau`() {
        val cleInitiale = 2
        val joueur = Joueur(id = 1, cle = cleInitiale, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(bourse = BourseScore(taille = 5)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(bourse = BourseScore(taille = 3)), position = HAUTMILIEU),
                CartePositionee(carte = villageois(), position = HAUTDROITE),
            )
        ))
        val carte = villageois(effets = Effets(effets = listOf(AjouteCleParCarteBourse())))
        val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitiale + 2)
    }

    @Test
    fun `ne doit pas ajouter de cle si aucune carte avec bourse n'est dans le tableau`() {
        val cleInitiale = 2
        val joueur = Joueur(id = 1, cle = cleInitiale)
        val carte = villageois(effets = Effets(effets = listOf(AjouteCleParCarteBourse())))
        val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitiale)
    }
}
