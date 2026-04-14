package com.chateaucombo.joueur.strategie

import com.chateaucombo.deck.Deck
import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.EffetScoreVide
import com.chateaucombo.deck.carte.effet.Effets
import com.chateaucombo.deck.carte.effet.effetpoint.AjoutePoints
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.strategie.ActionCle
import com.chateaucombo.strategie.StrategieAnticipatrice
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.*
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StrategieAnticipatriceTest {

    private lateinit var strategie: StrategieAnticipatrice

    @BeforeEach
    fun setUp() {
        strategie = StrategieAnticipatrice()
    }

    private fun carteAvecPoints(nom: String, points: Int, cout: Int = 0): Carte = Villageois(
        nom = nom, cout = cout, blasons = listOf(Blason.PAYSAN),
        effets = Effets(), effetScore = AjoutePoints(points)
    )

    private fun carteSansPoints(nom: String, cout: Int = 0): Carte = Villageois(
        nom = nom, cout = cout, blasons = listOf(Blason.PAYSAN),
        effets = Effets(), effetScore = EffetScoreVide
    )

    private fun deckActuel(cartes: List<Carte>) = Deck(
        cartesDisponibles = cartes.toMutableList(), cartes = mutableListOf(), estLeDeckActuel = true
    )

    private fun autreDeck(cartes: List<Carte>) = Deck(
        cartesDisponibles = cartes.toMutableList(), cartes = mutableListOf(), estLeDeckActuel = false
    )

    @Nested
    inner class PenaliteCleElevee {

        @Test
        fun `ne doit pas changer de deck si l'avantage est trop faible pour justifier la perte d'une cle`() {
            // Gain deck actuel = 3, deck autre = 4 : écart de 1 point < pénalité de 2 => RIEN
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(carteAvecPoints("Actuelle", 3))),
                autreDeck(listOf(carteAvecPoints("Autre", 4)))
            )

            val action = strategie.choisitActionCle(joueur, decks)

            assertThat(action).isEqualTo(ActionCle.RIEN)
        }

        @Test
        fun `doit changer de deck quand l'avantage dépasse clairement la pénalité de cle`() {
            // Gain deck actuel = 2, deck autre = 10 : écart de 8 points > pénalité => CHANGE_DECK
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(carteAvecPoints("Actuelle", 2))),
                autreDeck(listOf(carteAvecPoints("Autre", 10)))
            )

            val action = strategie.choisitActionCle(joueur, decks)

            assertThat(action).isEqualTo(ActionCle.CHANGE_DECK)
        }
    }

    @Nested
    inner class LookaheadProchainTour {

        @Test
        fun `doit preferer rester sur le deck qui garde une carte de repli interessante`() {
            // Deck actuel : meilleure carte = 5 pts, reste = 4 pts
            // Deck autre : meilleure carte = 6 pts, reste = 0 pts
            // Rester : 5 + discount * 4 = 5 + 2 = 7
            // Swapper : 6 - 3 (pénalité) + discount * 5 = 3 + 2.5 ≈ 5
            // => Rester
            val meilleureActuelle = carteAvecPoints("Actuelle #1", 5)
            val secondeActuelle = carteAvecPoints("Actuelle #2", 4)
            val meilleureAutre = carteAvecPoints("Autre #1", 6)
            val faibleAutre = carteAvecPoints("Autre #2", 0)
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(meilleureActuelle, secondeActuelle)),
                autreDeck(listOf(meilleureAutre, faibleAutre))
            )

            val action = strategie.choisitActionCle(joueur, decks)

            assertThat(action).isEqualTo(ActionCle.RIEN)
        }

        @Test
        fun `doit swapper quand le deck autre offre aussi un bon plan de repli`() {
            // Deck actuel : meilleure = 3, reste = 0
            // Deck autre : meilleure = 10, reste = 8
            // Rester : 3 + discount * 10 = 3 + 5 = 8  (peut piocher dans l'autre au tour suivant)
            // Swapper : 10 - 3 + discount * 8 = 7 + 4 = 11
            // => Swap
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(carteAvecPoints("Actuelle #1", 3), carteAvecPoints("Actuelle #2", 0))),
                autreDeck(listOf(carteAvecPoints("Autre #1", 10), carteAvecPoints("Autre #2", 8)))
            )

            val action = strategie.choisitActionCle(joueur, decks)

            assertThat(action).isEqualTo(ActionCle.CHANGE_DECK)
        }
    }

    @Nested
    inner class ChoisitUneCarte {

        @Test
        fun `doit renvoyer la carte en cache si elle est dans les cartes achetables`() {
            val carteMoinsBonne = carteSansPoints("Autre")
            val carteCible = carteAvecPoints("Cible", 10)
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(carteMoinsBonne, carteCible)),
                autreDeck(listOf(carteSansPoints("Autre deck")))
            )
            strategie.choisitActionCle(joueur, decks)

            val carteChoisie = strategie.choisitUneCarte(listOf(carteMoinsBonne, carteCible), emptyList())

            assertThat(carteChoisie).isEqualTo(carteCible)
        }

        @Test
        fun `doit renvoyer une carte verso si aucune carte n'est achetable`() {
            val cartesDisponibles = listOf(carteSansPoints("A"), carteSansPoints("B"))

            val carteChoisie = strategie.choisitUneCarte(emptyList(), cartesDisponibles)

            assertThat(carteChoisie).isInstanceOf(CarteVerso::class.java)
            assertThat(cartesDisponibles).contains((carteChoisie as CarteVerso).carteOriginale)
        }
    }

    @Nested
    inner class ChoisitUnePosition {

        @Test
        fun `doit renvoyer la position en cache calculee lors de choisitActionCle`() {
            // Tableau avec une carte en MILIEUMILIEU : nouvelle carte placée en position adjacente
            val tableau = Tableau(
                cartesPositionees = mutableListOf(CartePositionee(carteSansPoints("Existante"), MILIEUMILIEU))
            )
            val joueur = Joueur(id = 1, tableau = tableau)
            val decks = listOf(
                deckActuel(listOf(carteAvecPoints("Carte", 5))),
                autreDeck(listOf(carteSansPoints("Autre")))
            )
            strategie.choisitActionCle(joueur, decks)
            strategie.choisitUneCarte(listOf(carteAvecPoints("Carte", 5)), emptyList())

            val positionsAutorisees = listOf(HAUTMILIEU, MILIEUGAUCHE, MILIEUDROITE, BASMILIEU)
            val position = strategie.choisitUnePosition(positionsAutorisees)

            assertThat(positionsAutorisees).contains(position)
        }
    }
}
