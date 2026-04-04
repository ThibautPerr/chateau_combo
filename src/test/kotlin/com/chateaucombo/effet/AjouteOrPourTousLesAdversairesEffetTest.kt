package com.chateaucombo.effet

import com.chateaucombo.effet.effetplacement.AjouteOrPourTousLesAdversaires
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteOrPourTousLesAdversairesEffetTest : EffetTestBase() {
    @Test
    fun `doit ajouter de l'or a tous les adversaires`() {
        val orInitial = 2
        val joueurs = List(4) { Joueur(id = it, or = orInitial) }
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrPourTousLesAdversaires(2))))
        val context = EffetContext(
            joueurActuel = joueurs.first(),
            joueurs = joueurs,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        joueurs
            .filter { it.id != joueurs.first().id }
            .forEach { joueur ->
                assertThat(joueur.or).isEqualTo(orInitial + 2)
            }
    }

    @Test
    fun `ne doit pas ajouter d'or au joueur actuel`() {
        val orInitial = 2
        val joueurs = List(4) { Joueur(id = it, or = orInitial) }
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrPourTousLesAdversaires(2))))
        val context = EffetContext(
            joueurActuel = joueurs.first(),
            joueurs = joueurs,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        assertThat(joueurs.first().or).isEqualTo(orInitial)
    }

    @Test
    fun `doit ne rien faire s'il n'y a pas d'adversaires`() {
        val orInitial = 2
        val joueurActuel = Joueur(id = 0, or = orInitial)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrPourTousLesAdversaires(2))))
        val context = EffetContext(
            joueurActuel = joueurActuel,
            joueurs = listOf(joueurActuel),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        assertThat(joueurActuel.or).isEqualTo(orInitial)
    }
}
