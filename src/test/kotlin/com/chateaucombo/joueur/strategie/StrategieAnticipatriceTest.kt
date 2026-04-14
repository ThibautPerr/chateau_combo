package com.chateaucombo.joueur.strategie

import com.chateaucombo.deck.Deck
import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.EffetScoreVide
import com.chateaucombo.deck.carte.effet.Effets
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutChatelain
import com.chateaucombo.deck.carte.effet.effetpoint.AjoutePoints
import com.chateaucombo.deck.carte.effet.effetpoint.PointsSiCoin
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.strategie.ActionCle
import com.chateaucombo.strategie.DirectionDeplacement
import com.chateaucombo.strategie.StrategieAnticipatrice
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.BASMILIEU
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUDROITE
import com.chateaucombo.tableau.Position.MILIEUGAUCHE
import com.chateaucombo.tableau.Position.MILIEUMILIEU
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
    inner class ReductionCoutChatelain {

        @Test
        fun `doit acheter un chatelain couteux quand une reduction de cout le rend abordable`() {
            val carteAvecReduction = Villageois(
                nom = "Reducteur", cout = 0, blasons = listOf(Blason.PAYSAN),
                effets = Effets(effetsPassifs = listOf(ReduceCoutChatelain())),
                effetScore = EffetScoreVide
            )
            val tableau = Tableau(
                cartesPositionees = mutableListOf(CartePositionee(carteAvecReduction, MILIEUMILIEU))
            )
            val joueur = Joueur(id = 1, or = 2, tableau = tableau)
            val chatelainCher = Chatelain(
                nom = "Noble", cout = 3, blasons = listOf(Blason.NOBLE),
                effets = Effets(), effetScore = AjoutePoints(10)
            )
            val decks = listOf(
                deckActuel(listOf(chatelainCher)),
                autreDeck(listOf(carteSansPoints("Autre")))
            )

            strategie.choisitActionCle(joueur, decks)
            val carteChoisie = strategie.choisitUneCarte(listOf(chatelainCher), emptyList())

            assertThat(carteChoisie).isEqualTo(chatelainCher)
        }
    }

    @Nested
    inner class ChoisitActionCleAucuneCarteAbordable {

        @Test
        fun `doit renvoyer RIEN si aucune carte n'est abordable dans les deux decks`() {
            val joueur = Joueur(id = 1, or = 0)
            val decks = listOf(
                deckActuel(listOf(carteAvecPoints("Chere", 5, cout = 7))),
                autreDeck(listOf(carteAvecPoints("Chere aussi", 8, cout = 7)))
            )

            val action = strategie.choisitActionCle(joueur, decks)

            assertThat(action).isEqualTo(ActionCle.RIEN)
        }
    }

    @Nested
    inner class ChoisitUnDeplacement {

        @Test
        fun `doit renvoyer AUCUN si aucun deplacement n'ameliore le score`() {
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(carteSansPoints("Carte"))),
                autreDeck(listOf(carteSansPoints("Autre")))
            )

            strategie.choisitActionCle(joueur, decks)
            val direction = strategie.choisitUnDeplacement(joueur)

            assertThat(direction).isEqualTo(DirectionDeplacement.AUCUN)
        }

        @Test
        fun `doit renvoyer une direction qui cree un coin quand une carte PointsSiCoin est disponible`() {
            val tableau = Tableau(
                cartesPositionees = mutableListOf(CartePositionee(carteSansPoints("Existante"), MILIEUMILIEU))
            )
            val joueur = Joueur(id = 1, tableau = tableau)
            val carteCoin = Villageois(
                nom = "Coin", cout = 0, blasons = listOf(Blason.PAYSAN),
                effets = Effets(), effetScore = PointsSiCoin(5)
            )
            val decks = listOf(
                deckActuel(listOf(carteCoin)),
                autreDeck(listOf(carteSansPoints("Autre")))
            )

            strategie.choisitActionCle(joueur, decks)
            val direction = strategie.choisitUnDeplacement(joueur)

            assertThat(direction).isIn(
                DirectionDeplacement.GAUCHE, DirectionDeplacement.DROITE,
                DirectionDeplacement.HAUT, DirectionDeplacement.BAS
            )
        }

        @Test
        fun `doit evaluer le deplacement sur la base du tableau courant quand choisitActionCle n'a pas ete appele`() {
            val carteCoin = Villageois(
                nom = "Coin", cout = 0, blasons = listOf(Blason.PAYSAN),
                effets = Effets(), effetScore = PointsSiCoin(5)
            )
            val tableau = Tableau(
                cartesPositionees = mutableListOf(CartePositionee(carteCoin, MILIEUGAUCHE))
            )
            val joueur = Joueur(id = 1, tableau = tableau)

            val direction = strategie.choisitUnDeplacement(joueur)

            assertThat(direction).isIn(DirectionDeplacement.HAUT, DirectionDeplacement.BAS)
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
            val verso = carteChoisie as? CarteVerso ?: error("carteChoisie devrait être CarteVerso")
            assertThat(cartesDisponibles).contains(verso.carteOriginale)
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
