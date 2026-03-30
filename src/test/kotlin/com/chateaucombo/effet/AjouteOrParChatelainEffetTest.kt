package com.chateaucombo.effet

import com.chateaucombo.deck.model.CarteVerso
import com.chateaucombo.effet.model.AjouteOrParChatelain
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

class AjouteOrParChatelainEffetTest : EffetTestBase() {
    @Test
    fun `doit ajouter autant d'or que de chatelains sur le tableau du joueur`() {
        val orInitial = 2
        val tableauAvecTroisChatelains = tableauAvecTroisChatelains()
        val joueur = Joueur(id = 1, or = orInitial, tableau = tableauAvecTroisChatelains)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParChatelain())))
        val context = EffetContext(
            joueurActuel = joueur,
            joueurs = emptyList(),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial + 3)
    }

    private fun tableauAvecTroisChatelains() = Tableau(
        cartesPositionees = mutableListOf(
            CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
            CartePositionee(carte = chatelain(), position = HAUTMILIEU),
            CartePositionee(carte = chatelain(), position = HAUTDROITE),
        )
    )

    @Test
    fun `ne doit pas compter les chatelains face verso`() {
        val orInitial = 2
        val tableauAvecTroisChatelains = tableauAvecTroisChatelains()
        val carteVerso = CartePositionee(carte = chatelainVerso(), position = HAUTGAUCHE)
        tableauAvecTroisChatelains.cartesPositionees.add(carteVerso)
        val joueur = Joueur(id = 1, or = orInitial, tableau = tableauAvecTroisChatelains)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParChatelain())))
        val context = EffetContext(
            joueurActuel = joueur,
            joueurs = emptyList(),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial + 3)
    }

    private fun chatelainVerso() = CarteVerso(carteOriginale = chatelain())
}
