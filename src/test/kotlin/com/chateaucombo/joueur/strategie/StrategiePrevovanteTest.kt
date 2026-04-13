package com.chateaucombo.joueur.strategie

import com.chateaucombo.deck.Deck
import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.BourseScore
import com.chateaucombo.deck.carte.effet.EffetScoreVide
import com.chateaucombo.deck.carte.effet.Effets
import com.chateaucombo.deck.carte.effet.effetpoint.AjoutePoints
import com.chateaucombo.deck.carte.effet.effetpoint.PointsParOrDepose
import com.chateaucombo.deck.carte.effet.effetpoint.PointsSiCoin
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.strategie.ActionCle
import com.chateaucombo.strategie.DirectionDeplacement
import com.chateaucombo.strategie.StrategiePrevoyante
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.*
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StrategiePrevovanteTest {

    private lateinit var strategie: StrategiePrevoyante

    @BeforeEach
    fun setUp() {
        strategie = StrategiePrevoyante()
    }

    private fun carteAvecPoints(nom: String, points: Int, cout: Int = 0): Carte = Villageois(
        nom = nom, cout = cout, blasons = listOf(Blason.PAYSAN),
        effets = Effets(), effetScore = AjoutePoints(points)
    )

    private fun carteSansPoints(nom: String, cout: Int = 0): Carte = Villageois(
        nom = nom, cout = cout, blasons = listOf(Blason.PAYSAN),
        effets = Effets(), effetScore = EffetScoreVide
    )

    private fun carteAvecPointsSiCoin(nom: String, points: Int): Carte = Villageois(
        nom = nom, cout = 0, blasons = listOf(Blason.PAYSAN),
        effets = Effets(), effetScore = PointsSiCoin(points)
    )

    private fun carteAvecPointsParOrDepose(nom: String): Carte = Villageois(
        nom = nom, cout = 0, blasons = listOf(Blason.PAYSAN),
        effets = Effets(), effetScore = PointsParOrDepose()
    )

    private fun carteBourse(nom: String, taille: Int, orDepose: Int = 0): Carte =
        Villageois(
            nom = nom, cout = 0, blasons = listOf(Blason.PAYSAN),
            effets = Effets(), effetScore = EffetScoreVide,
            bourse = BourseScore(taille).also { it.orDepose = orDepose }
        )

    private fun deckActuel(cartes: List<Carte>) = Deck(
        cartesDisponibles = cartes.toMutableList(), cartes = mutableListOf(), estLeDeckActuel = true
    )

    private fun autreDeck(cartes: List<Carte>) = Deck(
        cartesDisponibles = cartes.toMutableList(), cartes = mutableListOf(), estLeDeckActuel = false
    )

    @Nested
    inner class ChoisitActionCle {

        @Test
        fun `doit renvoyer RIEN si la meilleure carte est dans le deck actuel`() {
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(carteAvecPoints("Bonne", 5))),
                autreDeck(listOf(carteAvecPoints("Moins bonne", 3)))
            )

            val action = strategie.choisitActionCle(joueur, decks)

            assertThat(action).isEqualTo(ActionCle.RIEN)
        }

        @Test
        fun `doit renvoyer CHANGE_DECK si la meilleure carte est dans l'autre deck apres deduction`() {
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(carteAvecPoints("Moins bonne", 3))),
                autreDeck(listOf(carteAvecPoints("Bonne", 5)))
            )

            val action = strategie.choisitActionCle(joueur, decks)

            assertThat(action).isEqualTo(ActionCle.CHANGE_DECK)
        }

        @Test
        fun `doit renvoyer RIEN si l'autre deck est seulement 1 point plus haut avant deduction`() {
            // autre deck : 4 pts, apres deduction 4-1=3 => egalite => pas de changement
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(carteAvecPoints("Egale", 3))),
                autreDeck(listOf(carteAvecPoints("Egale apres deduction", 4)))
            )

            val action = strategie.choisitActionCle(joueur, decks)

            assertThat(action).isEqualTo(ActionCle.RIEN)
        }

        @Test
        fun `doit mettre en cache la meilleure carte du deck actuel`() {
            val carteMoinsBonne = carteAvecPoints("Moins bonne", 2)
            val carteCible = carteAvecPoints("Meilleure", 5)
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(carteMoinsBonne, carteCible)),
                autreDeck(listOf(carteAvecPoints("Autre deck", 3)))
            )

            strategie.choisitActionCle(joueur, decks)
            val carteChoisie = strategie.choisitUneCarte(listOf(carteMoinsBonne, carteCible), emptyList())

            assertThat(carteChoisie).isEqualTo(carteCible)
        }

        @Nested
        inner class ScoreTheoriquePointsParOrDepose {

            @Test
            fun `doit preferer une carte PointsParOrDepose a une carte avec moins de points fixes`() {
                // Bourse taille=5 sur le tableau => score theorique de PointsParOrDepose = 5 > AjoutePoints(3)
                val bourse = carteBourse("Bourse", taille = 5)
                val tableau = Tableau(cartesPositionees = mutableListOf(CartePositionee(bourse, MILIEUMILIEU)))
                val joueur = Joueur(id = 1, tableau = tableau)
                val cartePointsParOrDepose = carteAvecPointsParOrDepose("PointsParOrDepose")
                val carteMoinsBonne = carteAvecPoints("Moins bonne", 3)
                val decks = listOf(
                    deckActuel(listOf(cartePointsParOrDepose, carteMoinsBonne)),
                    autreDeck(listOf(carteSansPoints("Autre")))
                )

                strategie.choisitActionCle(joueur, decks)
                val carteChoisie = strategie.choisitUneCarte(
                    listOf(cartePointsParOrDepose, carteMoinsBonne), emptyList()
                )

                assertThat(carteChoisie).isEqualTo(cartePointsParOrDepose)
            }

            @Test
            fun `doit preferer une nouvelle bourse si une carte PointsParOrDepose est deja sur le tableau`() {
                // PointsParOrDepose sur le tableau => ajouter bourse taille=5 apporte 5 pts de gain > AjoutePoints(3)
                val cartePointsParOrDepose = carteAvecPointsParOrDepose("PointsParOrDepose existante")
                val tableau = Tableau(
                    cartesPositionees = mutableListOf(CartePositionee(cartePointsParOrDepose, MILIEUMILIEU))
                )
                val joueur = Joueur(id = 1, tableau = tableau)
                val nouvelleBourse = carteBourse("Nouvelle bourse", taille = 5)
                val carteMoinsBonne = carteAvecPoints("Moins bonne", 3)
                val decks = listOf(
                    deckActuel(listOf(nouvelleBourse, carteMoinsBonne)),
                    autreDeck(listOf(carteSansPoints("Autre")))
                )

                strategie.choisitActionCle(joueur, decks)
                val carteChoisie = strategie.choisitUneCarte(
                    listOf(nouvelleBourse, carteMoinsBonne), emptyList()
                )

                assertThat(carteChoisie).isEqualTo(nouvelleBourse)
            }
        }
    }

    @Nested
    inner class ChoisitUnDeplacement {

        @Test
        fun `doit renvoyer AUCUN si aucun deplacement n'ameliore le score`() {
            // Tableau vide : tous les deplacements donnent score 0, AUCUN est choisi
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
            // Carte en MILIEUMILIEU : tout deplacement deplace la carte vers un bord et libere un coin adjacent
            val tableau = Tableau(
                cartesPositionees = mutableListOf(CartePositionee(carteSansPoints("Existante"), MILIEUMILIEU))
            )
            val joueur = Joueur(id = 1, tableau = tableau)
            val carteCoin = carteAvecPointsSiCoin("Coin", points = 5)
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
            // Carte PointsSiCoin en MILIEUGAUCHE : HAUT => HAUTGAUCHE (coin) ou BAS => BASGAUCHE (coin)
            val carteCoin = carteAvecPointsSiCoin("Coin", points = 5)
            val tableau = Tableau(
                cartesPositionees = mutableListOf(CartePositionee(carteCoin, MILIEUGAUCHE))
            )
            val joueur = Joueur(id = 1, tableau = tableau)

            // choisitActionCle n'est PAS appele (simule le cas cle == 0)
            val direction = strategie.choisitUnDeplacement(joueur)

            assertThat(direction).isIn(DirectionDeplacement.HAUT, DirectionDeplacement.BAS)
        }

        @Test
        fun `doit reinitialiser la direction en cache apres utilisation`() {
            val tableau = Tableau(
                cartesPositionees = mutableListOf(CartePositionee(carteSansPoints("Existante"), MILIEUMILIEU))
            )
            val joueur = Joueur(id = 1, tableau = tableau)
            val carteCoin = carteAvecPointsSiCoin("Coin", points = 5)
            val decks = listOf(
                deckActuel(listOf(carteCoin)),
                autreDeck(listOf(carteSansPoints("Autre")))
            )

            strategie.choisitActionCle(joueur, decks)
            strategie.choisitUnDeplacement(joueur) // consomme le cache

            // Sans re-calcul, tableau vide => tous les deplacements a score 0 => AUCUN
            val deuxiemeDirection = strategie.choisitUnDeplacement(Joueur(id = 1))

            assertThat(deuxiemeDirection).isEqualTo(DirectionDeplacement.AUCUN)
        }
    }

    @Nested
    inner class ChoisitUneCarte {

        @Test
        fun `doit renvoyer la carte en cache si elle est dans les cartes achetables`() {
            val carteMoinsBonne = carteSansPoints("Autre")
            val carteCible = carteAvecPoints("Cible", 5)
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
        fun `doit renvoyer une carte parmi les achetables si le cache est vide`() {
            val cartesAchetables = listOf(carteSansPoints("A"), carteSansPoints("B"))

            val carteChoisie = strategie.choisitUneCarte(cartesAchetables, cartesAchetables)

            assertThat(cartesAchetables).contains(carteChoisie)
        }

        @Test
        fun `doit renvoyer une carte verso si aucune carte n'est achetable`() {
            val cartesDisponibles = listOf(carteSansPoints("A"), carteSansPoints("B"))

            val carteChoisie = strategie.choisitUneCarte(emptyList(), cartesDisponibles)

            assertThat(carteChoisie).isInstanceOf(CarteVerso::class.java)
            assertThat(cartesDisponibles).contains((carteChoisie as CarteVerso).carteOriginale)
        }

        @Test
        fun `doit reinitialiser le cache apres utilisation`() {
            val carteCible = carteAvecPoints("Cible", 5)
            val joueur = Joueur(id = 1)
            val decks = listOf(deckActuel(listOf(carteCible)), autreDeck(listOf(carteSansPoints("Autre"))))
            strategie.choisitActionCle(joueur, decks)
            strategie.choisitUneCarte(listOf(carteCible), emptyList())

            val cartesAchetables = listOf(carteSansPoints("Nouveau A"), carteSansPoints("Nouveau B"))
            val deuxiemeChoix = strategie.choisitUneCarte(cartesAchetables, cartesAchetables)

            assertThat(cartesAchetables).contains(deuxiemeChoix)
        }
    }

    @Nested
    inner class ChoisitUnePosition {

        @Test
        fun `doit renvoyer la position en cache calculee lors de choisitActionCle`() {
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(carteAvecPoints("Carte", 5))),
                autreDeck(listOf(carteSansPoints("Autre")))
            )
            strategie.choisitActionCle(joueur, decks)
            strategie.choisitUneCarte(listOf(carteAvecPoints("Carte", 5)), emptyList())

            val position = strategie.choisitUnePosition(listOf(MILIEUMILIEU))

            assertThat(position).isEqualTo(MILIEUMILIEU)
        }

        @Test
        fun `doit renvoyer une position parmi les autorisees si le cache est vide`() {
            val positionsAutorisees = listOf(HAUTMILIEU, MILIEUGAUCHE, MILIEUDROITE, BASMILIEU)

            val position = strategie.choisitUnePosition(positionsAutorisees)

            assertThat(positionsAutorisees).contains(position)
        }

        @Test
        fun `doit reinitialiser le cache apres utilisation`() {
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(carteAvecPoints("Carte", 5))),
                autreDeck(listOf(carteSansPoints("Autre")))
            )
            strategie.choisitActionCle(joueur, decks)
            strategie.choisitUneCarte(listOf(carteAvecPoints("Carte", 5)), emptyList())
            strategie.choisitUnePosition(listOf(MILIEUMILIEU))

            val positionsAutorisees = listOf(HAUTMILIEU, MILIEUGAUCHE)
            val deuxiemePosition = strategie.choisitUnePosition(positionsAutorisees)

            assertThat(positionsAutorisees).contains(deuxiemePosition)
        }
    }
}
