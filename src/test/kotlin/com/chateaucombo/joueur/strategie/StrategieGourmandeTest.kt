package com.chateaucombo.joueur.strategie

import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.Deck
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.BourseScore
import com.chateaucombo.deck.carte.effet.EffetScoreVide
import com.chateaucombo.deck.carte.effet.Effets
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutChatelain
import com.chateaucombo.deck.carte.effet.effetpoint.AjoutePoints
import com.chateaucombo.deck.carte.effet.effetpoint.PointsParVillageois
import com.chateaucombo.deck.carte.effet.effetpoint.PointsSiCoin
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.strategie.ActionCle
import com.chateaucombo.strategie.DirectionDeplacement
import com.chateaucombo.strategie.StrategieGourmande
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.*
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StrategieGourmandeTest {

    private lateinit var strategie: StrategieGourmande

    @BeforeEach
    fun setUp() {
        strategie = StrategieGourmande()
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

    private fun carteBourse(nom: String, taille: Int): Carte =
        Villageois(
            nom = nom, cout = 0, blasons = listOf(Blason.PAYSAN),
            effets = Effets(), effetScore = EffetScoreVide,
            bourse = BourseScore(taille)
        )

    private fun carteAvecPointsParVillageois(nom: String, points: Int): Carte = Villageois(
        nom = nom, cout = 0, blasons = listOf(Blason.PAYSAN),
        effets = Effets(), effetScore = PointsParVillageois(points)
    )

    private fun chatelainAvecPoints(nom: String, points: Int): Carte = Chatelain(
        nom = nom, cout = 0, blasons = listOf(Blason.NOBLE),
        effets = Effets(), effetScore = AjoutePoints(points)
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
            // autre deck : 4 pts, apres deduction 4-1=3 pts => egalite avec deck actuel => RIEN
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(carteAvecPoints("Egale", 3))),
                autreDeck(listOf(carteAvecPoints("Egale apres deduction", 4)))
            )

            val action = strategie.choisitActionCle(joueur, decks)

            assertThat(action).isEqualTo(ActionCle.RIEN)
        }

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

        @Test
        fun `doit mettre en cache la meilleure carte de l'autre deck`() {
            val carteCible = carteAvecPoints("Meilleure autre deck", 10)
            val joueur = Joueur(id = 1)
            val decks = listOf(
                deckActuel(listOf(carteAvecPoints("Moins bonne", 2))),
                autreDeck(listOf(carteCible))
            )

            strategie.choisitActionCle(joueur, decks)
            val carteChoisie = strategie.choisitUneCarte(listOf(carteCible), emptyList())

            assertThat(carteChoisie).isEqualTo(carteCible)
        }
    }

    @Nested
    inner class ChoisitUneCarte {

        @Test
        fun `doit renvoyer la carte en cache si elle est dans les cartes achetables`() {
            val carteMoinsBonne = carteSansPoints("Autre 1")
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
            // Tableau vide : seule position possible = MILIEUMILIEU
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
        fun `doit choisir un coin pour une carte avec PointsSiCoin`() {
            // Tableau avec deux cartes en colonne centrale => les coins HAUTGAUCHE et HAUTDROITE sont disponibles
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carteSansPoints("Existante 1"), MILIEUMILIEU),
                    CartePositionee(carteSansPoints("Existante 2"), HAUTMILIEU),
                )
            )
            val joueur = Joueur(id = 1, tableau = tableau)
            val carteCoin = carteAvecPointsSiCoin("Coin", points = 5)
            val decks = listOf(
                deckActuel(listOf(carteCoin)),
                autreDeck(listOf(carteSansPoints("Autre")))
            )

            strategie.choisitActionCle(joueur, decks)
            val positionsAutorisees = listOf(HAUTGAUCHE, HAUTDROITE, MILIEUGAUCHE, MILIEUDROITE, BASMILIEU)
            strategie.choisitUneCarte(listOf(carteCoin), emptyList())
            val position = strategie.choisitUnePosition(positionsAutorisees)

            assertThat(position).isIn(HAUTGAUCHE, HAUTDROITE)
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

    @Nested
    inner class ChoisitUnDeplacement {

        @Test
        fun `doit toujours renvoyer AUCUN`() {
            val deplacement = strategie.choisitUnDeplacement(Joueur(id = 1))

            assertThat(deplacement).isEqualTo(DirectionDeplacement.AUCUN)
        }
    }

    @Nested
    inner class ReductionCoutChatelain {

        @Test
        fun `doit acheter un chatelain couteux quand une reduction de cout le rend abordable`() {
            // Carte existante avec ReduceCoutChatelain => chatelain cout 3 coute 2
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
    inner class EvaluationMarginale {

        @Test
        fun `doit preferer une carte bourse a une carte avec moins de points fixes`() {
            // Bourse taille=5 => valeur theorique 5*2=10 pts > AjoutePoints(3)
            val joueur = Joueur(id = 1)
            val carteBourse = carteBourse("Bourse", taille = 5)
            val carteMoinsBonne = carteAvecPoints("Moins bonne", 3)
            val decks = listOf(
                deckActuel(listOf(carteBourse, carteMoinsBonne)),
                autreDeck(listOf(carteSansPoints("Autre")))
            )

            strategie.choisitActionCle(joueur, decks)
            val carteChoisie = strategie.choisitUneCarte(listOf(carteBourse, carteMoinsBonne), emptyList())

            assertThat(carteChoisie).isEqualTo(carteBourse)
        }

        @Test
        fun `doit compter la synergie sur les cartes existantes dans l'evaluation`() {
            // Tableau avec PointsParVillageois(3) : ajouter un villageois synergique (score propre=0)
            // rapporte +3 (synergy) alors qu'un Chatelain AjoutePoints(2) ne rapporte que +2 sans synergy
            val carteExistante = carteAvecPointsParVillageois("Synergy", 3)
            val tableau = Tableau(cartesPositionees = mutableListOf(
                CartePositionee(carteExistante, MILIEUMILIEU)
            ))
            val joueur = Joueur(id = 1, tableau = tableau)
            val carteSynergique = carteSansPoints("Villageois synergique")       // Villageois : +3 synergy
            val carteMoinsBonne = chatelainAvecPoints("Chatelain", 2)            // Chatelain : +2 fixe, pas de synergy
            val decks = listOf(
                deckActuel(listOf(carteSynergique, carteMoinsBonne)),
                autreDeck(listOf(carteSansPoints("Autre")))
            )

            strategie.choisitActionCle(joueur, decks)
            val carteChoisie = strategie.choisitUneCarte(listOf(carteSynergique, carteMoinsBonne), emptyList())

            assertThat(carteChoisie).isEqualTo(carteSynergique)
        }
    }
}
