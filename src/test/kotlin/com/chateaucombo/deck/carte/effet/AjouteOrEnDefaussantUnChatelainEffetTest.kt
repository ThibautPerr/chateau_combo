package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.Deck
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrEnDefaussantUnChatelain
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class AjouteOrEnDefaussantUnChatelainEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit gagner autant d'or que le cout de la carte defaussee`() {
        val orInitial = 2
        val joueur = Joueur(id = 1, or = orInitial)
        val carteDisponible = Chatelain(cout = 5, nom = "carte", blasons = emptyList(), effets = Effets())
        val deckChatelains = Deck(nom = "Chatelains", cartes = mutableListOf(), cartesDisponibles = mutableListOf(carteDisponible), estLeDeckActuel = false)
        val carte = villageois(effets = Effets(effets = listOf(AjouteOrEnDefaussantUnChatelain())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = listOf(deckChatelains))

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial + 5)
    }

    @Test
    fun `doit retirer la carte defaussee des cartesDisponibles et l'ajouter a la defausse`() {
        val joueur = Joueur(id = 1)
        val carteDisponible = Chatelain(cout = 3, nom = "carte", blasons = emptyList(), effets = Effets())
        val deckChatelains = Deck(nom = "Chatelains", cartes = mutableListOf(), cartesDisponibles = mutableListOf(carteDisponible), estLeDeckActuel = false)
        val carte = villageois(effets = Effets(effets = listOf(AjouteOrEnDefaussantUnChatelain())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = listOf(deckChatelains))

        carte.effets.effets.first().apply(context)

        assertThat(deckChatelains.cartesDisponibles).doesNotContain(carteDisponible)
        assertThat(deckChatelains.defausse).contains(carteDisponible)
    }

    @Test
    fun `doit choisir la carte avec le cout le plus eleve`() {
        val orInitial = 2
        val joueur = Joueur(id = 1, or = orInitial)
        val carteCheap = Chatelain(cout = 1, nom = "Cheap", blasons = emptyList(), effets = Effets())
        val carteExpensive = Chatelain(cout = 6, nom = "Expensive", blasons = emptyList(), effets = Effets())
        val carteMedium = Chatelain(cout = 3, nom = "Medium", blasons = emptyList(), effets = Effets())
        val deckChatelains = Deck(nom = "Chatelains", cartes = mutableListOf(), cartesDisponibles = mutableListOf(carteCheap, carteExpensive, carteMedium), estLeDeckActuel = false)
        val carte = villageois(effets = Effets(effets = listOf(AjouteOrEnDefaussantUnChatelain())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = listOf(deckChatelains))

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial + 6)
        assertThat(deckChatelains.cartesDisponibles).doesNotContain(carteExpensive)
        assertThat(deckChatelains.defausse).contains(carteExpensive)
    }

    @Test
    fun `ne doit pas ajouter d'or si les cartesDisponibles sont vides`() {
        val orInitial = 2
        val joueur = Joueur(id = 1, or = orInitial)
        val deckChatelains = Deck(nom = "Chatelains", cartes = mutableListOf(), cartesDisponibles = mutableListOf(), estLeDeckActuel = false)
        val carte = villageois(effets = Effets(effets = listOf(AjouteOrEnDefaussantUnChatelain())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = listOf(deckChatelains))

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial)
    }

    @Test
    fun `doit lever une erreur si le deck chatelains est absent`() {
        val joueur = Joueur(id = 1)
        val carte = villageois(effets = Effets(effets = listOf(AjouteOrEnDefaussantUnChatelain())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        assertThatThrownBy { carte.effets.effets.first().apply(context) }
            .isInstanceOf(IllegalStateException::class.java)
    }
}
