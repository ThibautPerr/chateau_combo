package com.chateaucombo.effet

import com.chateaucombo.deck.model.Deck
import com.chateaucombo.effet.model.AjouteCleEnDefaussantUnVillageois
import com.chateaucombo.effet.model.EffetContext
import com.chateaucombo.effet.model.Effets
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class AjouteCleEnDefaussantUnVillageoisEffetTest : EffetTestBase() {
    @Test
    fun `doit gagner autant de cles que le cout de la carte defaussee`() {
        val cleInitiale = 2
        val joueur = Joueur(id = 1, cle = cleInitiale)
        val carteDisponible = villageois(cout = 5)
        val deckVillageois = Deck(nom = "Villageois", cartes = mutableListOf(), cartesDisponibles = mutableListOf(carteDisponible), estLeDeckActuel = true)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleEnDefaussantUnVillageois())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = listOf(deckVillageois))

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitiale + 5)
    }

    @Test
    fun `doit retirer la carte defaussee des cartesDisponibles et l'ajouter a la defausse`() {
        val joueur = Joueur(id = 1)
        val carteDisponible = villageois(cout = 3)
        val deckVillageois = Deck(nom = "Villageois", cartes = mutableListOf(), cartesDisponibles = mutableListOf(carteDisponible), estLeDeckActuel = true)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleEnDefaussantUnVillageois())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = listOf(deckVillageois))

        carte.effets.effets.first().apply(context)

        assertThat(deckVillageois.cartesDisponibles).doesNotContain(carteDisponible)
        assertThat(deckVillageois.defausse).contains(carteDisponible)
    }

    @Test
    fun `doit choisir la carte avec le cout le plus eleve`() {
        val cleInitiale = 2
        val joueur = Joueur(id = 1, cle = cleInitiale)
        val carteCheap = villageois(cout = 2)
        val carteExpensive = villageois(cout = 6)
        val carteMedium = villageois(cout = 4)
        val deckVillageois = Deck(nom = "Villageois", cartes = mutableListOf(), cartesDisponibles = mutableListOf(carteCheap, carteExpensive, carteMedium), estLeDeckActuel = true)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleEnDefaussantUnVillageois())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = listOf(deckVillageois))

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitiale + 6)
        assertThat(deckVillageois.cartesDisponibles).doesNotContain(carteExpensive)
        assertThat(deckVillageois.defausse).contains(carteExpensive)
    }

    @Test
    fun `ne doit pas ajouter de cles si les cartesDisponibles sont vides`() {
        val cleInitiale = 2
        val joueur = Joueur(id = 1, cle = cleInitiale)
        val deckVillageois = Deck(nom = "Villageois", cartes = mutableListOf(), cartesDisponibles = mutableListOf(), estLeDeckActuel = true)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleEnDefaussantUnVillageois())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = listOf(deckVillageois))

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitiale)
    }

    @Test
    fun `doit lever une erreur si le deck villageois est absent`() {
        val joueur = Joueur(id = 1)
        val carte = chatelain(effets = Effets(effets = listOf(AjouteCleEnDefaussantUnVillageois())))
        val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

        assertThatThrownBy { carte.effets.effets.first().apply(context) }
            .isInstanceOf(IllegalStateException::class.java)
    }
}
