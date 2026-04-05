package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrDansBourses
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteOrDansBoursesEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit ajouter deux ors dans chaque bourse ayant de la place`() {
        val bourse = BourseScore(taille = 5)
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(CartePositionee(carte = villageois(bourse = bourse), position = HAUTGAUCHE))
        ))
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrDansBourses(or = 2))))
        val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

        carte.effets.effets.first().apply(context)

        assertThat(bourse.orDepose).isEqualTo(2)
    }

    @Test
    fun `doit ajouter dans chaque bourse ayant de la place`() {
        val bourse1 = BourseScore(taille = 5)
        val bourse2 = BourseScore(taille = 3)
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(bourse = bourse1), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(bourse = bourse2), position = HAUTMILIEU),
            )
        ))
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrDansBourses(or = 2))))
        val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

        carte.effets.effets.first().apply(context)

        assertThat(bourse1.orDepose).isEqualTo(2)
        assertThat(bourse2.orDepose).isEqualTo(2)
    }

    @Test
    fun `ne doit pas depasser la taille de la bourse`() {
        val bourse = BourseScore(taille = 3)
        bourse.orDepose = 2
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(CartePositionee(carte = villageois(bourse = bourse), position = HAUTGAUCHE))
        ))
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrDansBourses(or = 2))))
        val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

        carte.effets.effets.first().apply(context)

        assertThat(bourse.orDepose).isEqualTo(3)
    }

    @Test
    fun `ne doit pas ajouter d'or dans une bourse deja pleine`() {
        val bourse = BourseScore(taille = 3)
        bourse.orDepose = 3
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(CartePositionee(carte = villageois(bourse = bourse), position = HAUTGAUCHE))
        ))
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrDansBourses(or = 2))))
        val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

        carte.effets.effets.first().apply(context)

        assertThat(bourse.orDepose).isEqualTo(3)
    }
}
