package com.chateaucombo.effet

import com.chateaucombo.deck.model.CarteVerso
import com.chateaucombo.effet.effetplacement.AjouteOrParVillageois
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteOrParVillageoisEffetTest : EffetTestBase() {
    @Test
    fun `doit ajouter autant d'or que de villageois sur le tableau du joueur`() {
        val orInitial = 2
        val tableauAvecTroisVillageois = tableauAvecTroisVillageois()
        val joueur = Joueur(id = 1, or = orInitial, tableau = tableauAvecTroisVillageois)
        val carte = villageois(effets = Effets(effets = listOf(AjouteOrParVillageois())))
        val context = EffetContext(
            joueurActuel = joueur,
            joueurs = emptyList(),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial + 3)
    }

    private fun tableauAvecTroisVillageois() = Tableau(
        cartesPositionees = mutableListOf(
            CartePositionee(carte = villageois(), position = HAUTGAUCHE),
            CartePositionee(carte = villageois(), position = HAUTMILIEU),
            CartePositionee(carte = villageois(), position = HAUTDROITE),
        )
    )

    @Test
    fun `ne doit pas compter les villageois face verso`() {
        val orInitial = 2
        val tableauAvecTroisChatelains = tableauAvecTroisVillageois()
        val carteVerso = CartePositionee(carte = villageoisVerso(), position = HAUTGAUCHE)
        tableauAvecTroisChatelains.cartesPositionees.add(carteVerso)
        val joueur = Joueur(id = 1, or = orInitial, tableau = tableauAvecTroisChatelains)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParVillageois())))
        val context = EffetContext(
            joueurActuel = joueur,
            joueurs = emptyList(),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial + 3)
    }

    private fun villageoisVerso() = CarteVerso(carteOriginale = villageois())
}
