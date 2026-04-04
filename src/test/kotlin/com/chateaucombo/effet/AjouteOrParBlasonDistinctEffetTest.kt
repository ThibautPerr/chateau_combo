package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason.MILITAIRE
import com.chateaucombo.deck.model.Blason.NOBLE
import com.chateaucombo.deck.model.Blason.RELIGIEUX
import com.chateaucombo.effet.effetplacement.AjouteOrParBlasonDistinct
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteOrParBlasonDistinctEffetTest : EffetTestBase() {
    @Test
    fun `doit ajouter autant d'or que de types de blasons distincts sur le tableau`() {
        val orInitial = 2
        val tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, NOBLE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(RELIGIEUX)), position = HAUTMILIEU),
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTDROITE),
            )
        )
        val joueur = Joueur(id = 1, or = orInitial, tableau = tableau)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDistinct())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial + 3) // MILITAIRE, NOBLE, RELIGIEUX
    }

    @Test
    fun `ne doit compter les blasons en double qu'une seule fois`() {
        val orInitial = 2
        val tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, MILITAIRE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTMILIEU),
            )
        )
        val joueur = Joueur(id = 1, or = orInitial, tableau = tableau)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDistinct())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial + 1) // only MILITAIRE
    }

    @Test
    fun `ne doit pas ajouter d'or si le tableau est vide`() {
        val orInitial = 2
        val joueur = Joueur(id = 1, or = orInitial)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDistinct())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial)
    }
}
