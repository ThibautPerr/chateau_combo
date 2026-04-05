package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.Blason.MILITAIRE
import com.chateaucombo.deck.carte.Blason.NOBLE
import com.chateaucombo.deck.carte.Blason.RELIGIEUX
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCleParBlasonDistinct
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTDROITE
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteCleParBlasonDistinctEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit ajouter autant de cles que de types de blasons distincts sur le tableau`() {
        val cleInitiale = 2
        val tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, NOBLE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(RELIGIEUX)), position = HAUTMILIEU),
                CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTDROITE),
            )
        )
        val joueur = Joueur(id = 1, cle = cleInitiale, tableau = tableau)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDistinct())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitiale + 3) // MILITAIRE, NOBLE, RELIGIEUX
    }

    @Test
    fun `ne doit compter les blasons en double qu'une seule fois`() {
        val cleInitiale = 2
        val tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, MILITAIRE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTMILIEU),
            )
        )
        val joueur = Joueur(id = 1, cle = cleInitiale, tableau = tableau)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDistinct())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitiale + 1) // only MILITAIRE
    }

    @Test
    fun `ne doit pas ajouter de cles si le tableau est vide`() {
        val cleInitiale = 2
        val joueur = Joueur(id = 1, cle = cleInitiale)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDistinct())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitiale)
    }
}
