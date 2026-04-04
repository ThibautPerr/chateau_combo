package com.chateaucombo.effet

import com.chateaucombo.effet.effetplacement.AjouteOrParEmplacementVide
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteOrParEmplacementVideEffetTest : EffetTestBase() {
    @Test
    fun `doit ajouter autant d'or que d'emplacements vides sur le tableau`() {
        val orInitial = 2
        val tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(), position = HAUTMILIEU),
                CartePositionee(carte = chatelain(), position = HAUTDROITE),
            )
        )
        val joueur = Joueur(id = 1, or = orInitial, tableau = tableau)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParEmplacementVide())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial + 6) // 9 - 3 = 6 emplacements vides
    }

    @Test
    fun `doit ajouter neuf or si le tableau est vide`() {
        val orInitial = 2
        val joueur = Joueur(id = 1, or = orInitial)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParEmplacementVide())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial + 9)
    }
}
