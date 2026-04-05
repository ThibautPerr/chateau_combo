package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCleParChatelain
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTDROITE
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteCleParChatelainEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit ajouter autant de cles que de chatelains sur le tableau du joueur`() {
        val cleInitial = 2
        val tableauAvecTroisChatelains = tableauAvecTroisChatelains()
        val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableauAvecTroisChatelains)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParChatelain())))
        val context = EffetContext(
            joueurActuel = joueur,
            joueurs = emptyList(),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitial + 3)
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
        val cleInitial = 2
        val tableauAvecTroisChatelains = tableauAvecTroisChatelains()
        val carteVerso = CartePositionee(carte = chatelainVerso(), position = HAUTGAUCHE)
        tableauAvecTroisChatelains.cartesPositionees.add(carteVerso)
        val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableauAvecTroisChatelains)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParChatelain())))
        val context = EffetContext(
            joueurActuel = joueur,
            joueurs = emptyList(),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitial + 3)
    }

    private fun chatelainVerso() = CarteVerso(carteOriginale = chatelain())
}
