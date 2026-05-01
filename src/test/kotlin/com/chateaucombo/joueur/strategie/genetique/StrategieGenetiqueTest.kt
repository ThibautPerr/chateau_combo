package com.chateaucombo.joueur.strategie.genetique

import com.chateaucombo.deck.Deck
import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.EffetScoreVide
import com.chateaucombo.deck.carte.effet.Effets
import com.chateaucombo.deck.carte.effet.effetpoint.AjoutePoints
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.strategie.ActionCle
import com.chateaucombo.strategie.DirectionDeplacement
import com.chateaucombo.strategie.genetique.ExtracteurFeatures
import com.chateaucombo.strategie.genetique.Genome
import com.chateaucombo.strategie.genetique.StrategieGenetique
import com.chateaucombo.tableau.Position
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StrategieGenetiqueTest {

    private fun carteAvecPoints(nom: String, points: Int, cout: Int = 0): Carte = Villageois(
        nom = nom, cout = cout, blasons = listOf(Blason.PAYSAN),
        effets = Effets(), effetScore = AjoutePoints(points),
    )

    private fun carteSansPoints(nom: String, cout: Int = 0): Carte = Villageois(
        nom = nom, cout = cout, blasons = listOf(Blason.PAYSAN),
        effets = Effets(), effetScore = EffetScoreVide,
    )

    private fun chatelainAvecPoints(nom: String, points: Int): Carte = Chatelain(
        nom = nom, cout = 0, blasons = listOf(Blason.NOBLE),
        effets = Effets(), effetScore = AjoutePoints(points),
    )

    private fun deckActuel(cartes: List<Carte>) = Deck(
        cartesDisponibles = cartes.toMutableList(), cartes = mutableListOf(), estLeDeckActuel = true,
    )

    private fun autreDeck(cartes: List<Carte>) = Deck(
        cartesDisponibles = cartes.toMutableList(), cartes = mutableListOf(), estLeDeckActuel = false,
    )

    @Test
    fun `choisitUnDeplacement renvoie toujours AUCUN`() {
        val strategie = StrategieGenetique()

        assertThat(strategie.choisitUnDeplacement(Joueur(id = 0))).isEqualTo(DirectionDeplacement.AUCUN)
    }

    @Test
    fun `choisitActionCle renvoie RIEN si la meilleure carte est dans le deck actuel`() {
        val strategie = StrategieGenetique()
        val joueur = Joueur(id = 0)
        val decks = listOf(
            deckActuel(listOf(carteAvecPoints("Bonne", 8))),
            autreDeck(listOf(carteAvecPoints("Faible", 2))),
        )

        val action = strategie.choisitActionCle(joueur, decks)

        assertThat(action).isEqualTo(ActionCle.RIEN)
    }

    @Test
    fun `choisitActionCle renvoie CHANGE_DECK si l'autre deck a une bien meilleure carte`() {
        val strategie = StrategieGenetique()
        val joueur = Joueur(id = 0)
        val decks = listOf(
            deckActuel(listOf(carteAvecPoints("Faible", 1))),
            autreDeck(listOf(carteAvecPoints("Excellente", 20))),
        )

        val action = strategie.choisitActionCle(joueur, decks)

        assertThat(action).isEqualTo(ActionCle.CHANGE_DECK)
    }

    @Test
    fun `choisitActionCle renvoie RIEN si le joueur n'a pas de cle disponible`() {
        val strategie = StrategieGenetique()
        val joueur = Joueur(id = 0, cle = 0)
        val decks = listOf(
            deckActuel(listOf(carteAvecPoints("Faible", 1))),
            autreDeck(listOf(carteAvecPoints("Excellente", 20))),
        )

        val action = strategie.choisitActionCle(joueur, decks)

        assertThat(action).isEqualTo(ActionCle.RIEN)
    }

    @Test
    fun `choisitActionCle met en cache la meilleure carte du deck choisi`() {
        val strategie = StrategieGenetique()
        val moinsBonne = carteAvecPoints("Moins bonne", 2)
        val cible = carteAvecPoints("Cible", 10)
        val joueur = Joueur(id = 0)
        val decks = listOf(deckActuel(listOf(moinsBonne, cible)), autreDeck(emptyList()))

        strategie.choisitActionCle(joueur, decks)
        val carteChoisie = strategie.choisitUneCarte(listOf(moinsBonne, cible), emptyList())

        assertThat(carteChoisie).isEqualTo(cible)
    }

    @Test
    fun `choisitUneCarte retourne une carte verso si rien n'est achetable`() {
        val strategie = StrategieGenetique()
        val cartesDisponibles = listOf(carteSansPoints("X"), carteSansPoints("Y"))

        val choisie = strategie.choisitUneCarte(cartesAchetables = emptyList(), cartesDisponibles = cartesDisponibles)

        assertThat(choisie).isInstanceOf(CarteVerso::class.java)
        assertThat(cartesDisponibles).contains((choisie as CarteVerso).carteOriginale)
    }

    @Test
    fun `choisitUneCarte retombe sur cartes achetables aleatoires si le cache est vide`() {
        val strategie = StrategieGenetique()
        val achetables = listOf(carteSansPoints("A"), carteSansPoints("B"))

        val choisie = strategie.choisitUneCarte(achetables, achetables)

        assertThat(achetables).contains(choisie)
    }

    @Test
    fun `un genome valorisant les chatelains les prefere aux villageois`() {
        // poids : feature[4] (chatelain) = 100, tout le reste = 0 → la carte chatelain doit gagner
        val poids = FloatArray(ExtracteurFeatures.NB_FEATURES).also { it[4] = 100f }
        val strategie = StrategieGenetique(genome = Genome(poids))

        val villageois = carteAvecPoints("V", 10)
        val chatelain = chatelainAvecPoints("C", 1)
        val joueur = Joueur(id = 0)
        val decks = listOf(deckActuel(listOf(villageois, chatelain)), autreDeck(emptyList()))

        strategie.choisitActionCle(joueur, decks)
        val choisie = strategie.choisitUneCarte(listOf(villageois, chatelain), emptyList())

        assertThat(choisie).isEqualTo(chatelain)
    }

    @Test
    fun `un genome qui valorise le centre y place la premiere carte`() {
        val poids = FloatArray(ExtracteurFeatures.NB_FEATURES).also { it[9] = 1f }
        val strategie = StrategieGenetique(genome = Genome(poids))
        val joueur = Joueur(id = 0)
        val decks = listOf(deckActuel(listOf(carteAvecPoints("C", 5))), autreDeck(emptyList()))

        strategie.choisitActionCle(joueur, decks)
        strategie.choisitUneCarte(listOf(carteAvecPoints("C", 5)), emptyList())
        val position = strategie.choisitUnePosition(listOf(Position.MILIEUMILIEU))

        assertThat(position).isEqualTo(Position.MILIEUMILIEU)
    }
}
