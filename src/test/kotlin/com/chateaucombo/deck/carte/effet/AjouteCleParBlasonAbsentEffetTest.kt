package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.Blason.MILITAIRE
import com.chateaucombo.deck.carte.Blason.NOBLE
import com.chateaucombo.deck.carte.Blason.RELIGIEUX
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCleParBlasonAbsent
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteCleParBlasonAbsentEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit ajouter autant de cles que de types de blasons absents du tableau`() {
        val cleInitiale = 2
        val tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, NOBLE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(RELIGIEUX)), position = HAUTMILIEU),
            )
        )
        val joueur = Joueur(id = 1, cle = cleInitiale, tableau = tableau)
        val carte = villageois(effets = Effets(effets = listOf(AjouteCleParBlasonAbsent())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitiale + 3) // ERUDIT, ARTISAN, PAYSAN absents
    }

    @Test
    fun `ne doit compter les blasons presents en double qu'une seule fois`() {
        val cleInitiale = 2
        val tableau = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, MILITAIRE)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTMILIEU),
            )
        )
        val joueur = Joueur(id = 1, cle = cleInitiale, tableau = tableau)
        val carte = villageois(effets = Effets(effets = listOf(AjouteCleParBlasonAbsent())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitiale + 5) // NOBLE, RELIGIEUX, ERUDIT, ARTISAN, PAYSAN absents
    }

    @Test
    fun `doit ajouter six cles si le tableau est vide`() {
        val cleInitiale = 2
        val joueur = Joueur(id = 1, cle = cleInitiale)
        val carte = villageois(effets = Effets(effets = listOf(AjouteCleParBlasonAbsent())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitiale + 6) // tous les blasons absents
    }
}
