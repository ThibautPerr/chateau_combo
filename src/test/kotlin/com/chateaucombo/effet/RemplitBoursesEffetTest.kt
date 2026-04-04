package com.chateaucombo.effet

import com.chateaucombo.effet.effetplacement.RemplitBourses
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RemplitBoursesEffetTest : EffetTestBase() {
    @Test
    fun `doit deposer l'or dans la bourse sans modifier l'or du joueur`() {
        val bourse = BourseScore(taille = 5)
        val joueur = Joueur(id = 1, or = 3, tableau = Tableau(
            cartesPositionees = mutableListOf(CartePositionee(carte = villageois(bourse = bourse), position = HAUTGAUCHE))
        ))
        val carte = chatelain(effets = Effets(effets = listOf(RemplitBourses(nb = 2))))
        val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

        carte.effets.effets.first().apply(context)

        assertThat(bourse.orDepose).isEqualTo(5)
        assertThat(joueur.or).isEqualTo(3)
    }

    @Test
    fun `doit remplir au maximum deux bourses en choisissant les plus grandes`() {
        val bourse4 = BourseScore(taille = 4)
        val bourse6 = BourseScore(taille = 6)
        val bourse3 = BourseScore(taille = 3)
        val joueur = Joueur(id = 1, tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(bourse = bourse4), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(bourse = bourse6), position = HAUTMILIEU),
                CartePositionee(carte = villageois(bourse = bourse3), position = HAUTDROITE),
            )
        ))
        val carte = chatelain(effets = Effets(effets = listOf(RemplitBourses(nb = 2))))
        val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

        carte.effets.effets.first().apply(context)

        assertThat(bourse6.orDepose).isEqualTo(6)
        assertThat(bourse4.orDepose).isEqualTo(4)
        assertThat(bourse3.orDepose).isEqualTo(0)
    }

    @Test
    fun `ne doit pas modifier les bourses si aucune carte avec bourse n'est dans le tableau`() {
        val joueur = Joueur(id = 1)
        val carte = chatelain(effets = Effets(effets = listOf(RemplitBourses(nb = 2))))
        val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(15)
    }
}
