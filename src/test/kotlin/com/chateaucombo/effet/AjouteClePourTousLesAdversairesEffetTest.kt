package com.chateaucombo.effet

import com.chateaucombo.effet.model.AjouteClePourTousLesAdversaires
import com.chateaucombo.effet.model.EffetContext
import com.chateaucombo.effet.model.Effets
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteClePourTousLesAdversairesEffetTest : EffetTestBase() {
    @Test
    fun `doit ajouter une cle a tous les adversaires mais pas au joueur actuel`() {
        val cleInitiale = 2
        val joueurs = List(4) { Joueur(id = it, cle = cleInitiale) }
        val carte = villageois(effets = Effets(effets = listOf(AjouteClePourTousLesAdversaires(1))))
        val context = EffetContext(
            joueurActuel = joueurs.first(),
            joueurs = joueurs,
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        assertThat(joueurs.first().cle).isEqualTo(cleInitiale)
        joueurs
            .filter { it.id != joueurs.first().id }
            .forEach { joueur ->
                assertThat(joueur.cle).isEqualTo(cleInitiale + 1)
            }
    }

    @Test
    fun `doit ne rien faire s'il n'y a pas d'adversaires`() {
        val cleInitiale = 2
        val joueurActuel = Joueur(id = 0, cle = cleInitiale)
        val carte = villageois(effets = Effets(effets = listOf(AjouteClePourTousLesAdversaires(1))))
        val context = EffetContext(
            joueurActuel = joueurActuel,
            joueurs = listOf(joueurActuel),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        assertThat(joueurActuel.cle).isEqualTo(cleInitiale)
    }
}
