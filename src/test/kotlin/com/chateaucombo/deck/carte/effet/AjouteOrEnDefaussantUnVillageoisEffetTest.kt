package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.Deck
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrEnDefaussantUnVillageois
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class AjouteOrEnDefaussantUnVillageoisEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @Test
    fun `doit gagner autant d'or que le cout de la carte defaussee`() {
        val orInitial = 2
        val joueur = Joueur(id = 1, or = orInitial)
        val carteDisponible = villageois(cout = 5)
        val deckVillageois = Deck(nom = "Villageois", cartes = mutableListOf(), cartesDisponibles = mutableListOf(carteDisponible), estLeDeckActuel = true)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrEnDefaussantUnVillageois())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = listOf(deckVillageois))

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial + 5)
    }

    @Test
    fun `doit retirer la carte defaussee des cartesDisponibles et l'ajouter a la defausse`() {
        val joueur = Joueur(id = 1)
        val carteDisponible = villageois(cout = 3)
        val deckVillageois = Deck(nom = "Villageois", cartes = mutableListOf(), cartesDisponibles = mutableListOf(carteDisponible), estLeDeckActuel = true)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrEnDefaussantUnVillageois())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = listOf(deckVillageois))

        carte.effets.effets.first().apply(context)

        assertThat(deckVillageois.cartesDisponibles).doesNotContain(carteDisponible)
        assertThat(deckVillageois.defausse).contains(carteDisponible)
    }

    @Test
    fun `doit choisir la carte avec le cout le plus eleve`() {
        val orInitial = 2
        val joueur = Joueur(id = 1, or = orInitial)
        val carteCheap = villageois(cout = 2)
        val carteExpensive = villageois(cout = 6)
        val carteMedium = villageois(cout = 4)
        val deckVillageois = Deck(nom = "Villageois", cartes = mutableListOf(), cartesDisponibles = mutableListOf(carteCheap, carteExpensive, carteMedium), estLeDeckActuel = true)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrEnDefaussantUnVillageois())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = listOf(deckVillageois))

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial + 6)
        assertThat(deckVillageois.cartesDisponibles).doesNotContain(carteExpensive)
        assertThat(deckVillageois.defausse).contains(carteExpensive)
    }

    @Test
    fun `ne doit pas ajouter d'or si les cartesDisponibles sont vides`() {
        val orInitial = 2
        val joueur = Joueur(id = 1, or = orInitial)
        val deckVillageois = Deck(nom = "Villageois", cartes = mutableListOf(), cartesDisponibles = mutableListOf(), estLeDeckActuel = true)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrEnDefaussantUnVillageois())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = listOf(deckVillageois))

        carte.effets.effets.first().apply(context)

        assertThat(joueur.or).isEqualTo(orInitial)
    }

    @Test
    fun `doit lever une erreur si le deck villageois est absent`() {
        val joueur = Joueur(id = 1)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteOrEnDefaussantUnVillageois())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        assertThatThrownBy { carte.effets.effets.first().apply(context) }
            .isInstanceOf(IllegalStateException::class.java)
    }
}
