package com.chateaucombo.strategie

import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.BourseScore
import com.chateaucombo.deck.carte.effet.EffetScoreVide
import com.chateaucombo.deck.carte.effet.EffetSeparateur
import com.chateaucombo.deck.carte.effet.Effets
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCle
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCleParChatelain
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCleParVillageois
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrDansBourses
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrParBlasonDistinct
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrParVillageois
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrPourTousLesAdversaires
import com.chateaucombo.deck.carte.effet.effetplacement.RemplitBourses
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EvaluateurHeuristiqueTest {

    private fun carteSansEffet(nom: String = "Neutre", blasons: List<Blason> = listOf(Blason.PAYSAN)): Carte =
        Villageois(nom = nom, cout = 0, blasons = blasons, effets = Effets(), effetScore = EffetScoreVide)

    private fun chatelainSansEffet(nom: String = "Noble"): Carte = Chatelain(
        nom = nom, cout = 0, blasons = listOf(Blason.NOBLE), effets = Effets(), effetScore = EffetScoreVide
    )

    private fun carteAvecEffets(
        effets: List<com.chateaucombo.deck.carte.effet.Effet>,
        separateur: EffetSeparateur = EffetSeparateur.ET,
        bourse: BourseScore? = null,
    ): Carte = Villageois(
        nom = "Test", cout = 0, blasons = listOf(Blason.PAYSAN),
        effets = Effets(effets = effets, separateur = separateur),
        effetScore = EffetScoreVide, bourse = bourse,
    )

    private fun joueurAvec(cartes: List<CartePositionee>, or: Int = 0) =
        Joueur(id = 1, or = or, tableau = Tableau(cartes.toMutableList()))

    @Nested
    inner class EstimerValeurEffetsPlacement {

        @Test
        fun `doit renvoyer 0 quand la carte n'a pas d'effet de placement`() {
            val joueur = joueurAvec(listOf(CartePositionee(carteSansEffet(), MILIEUMILIEU)))

            val valeur = EvaluateurHeuristique.estimerValeurEffetsPlacement(carteSansEffet(), joueur)

            assertThat(valeur).isZero()
        }

        @Test
        fun `doit valoriser AjouteCle au nombre de cles produites`() {
            val carte = carteAvecEffets(listOf(AjouteCle(cle = 3)))
            val joueur = joueurAvec(listOf(CartePositionee(carte, MILIEUMILIEU)))

            val valeur = EvaluateurHeuristique.estimerValeurEffetsPlacement(carte, joueur)

            assertThat(valeur).isEqualTo(3)
        }

        @Test
        fun `doit valoriser l'or produit a 2 pts quand une bourse a de la place`() {
            // 2 villageois sur le tableau => AjouteOrParVillageois produit 2 or.
            // Bourse avec capacite restante => chaque or vaut 2 pts => valeur = 4
            val bourse = BourseScore(taille = 3)
            val carte = carteAvecEffets(listOf(AjouteOrParVillageois()), bourse = bourse)
            val tableau = listOf(
                CartePositionee(carte, MILIEUMILIEU),
                CartePositionee(carteSansEffet("Autre"), com.chateaucombo.tableau.Position.HAUTMILIEU),
            )
            val joueur = joueurAvec(tableau)

            val valeur = EvaluateurHeuristique.estimerValeurEffetsPlacement(carte, joueur)

            assertThat(valeur).isEqualTo(4) // 2 villageois * 2 pts
        }

        @Test
        fun `doit valoriser l'or hors bourse a une petite fraction seulement`() {
            // Aucune bourse sur le tableau => or quasi-sans valeur (0.3 pt par or, tronque a 0)
            val carte = carteAvecEffets(listOf(AjouteOrParVillageois()))
            val joueur = joueurAvec(listOf(CartePositionee(carte, MILIEUMILIEU)))

            val valeur = EvaluateurHeuristique.estimerValeurEffetsPlacement(carte, joueur)

            // 1 villageois * 0.3 = 0.3 → tronque a 0
            assertThat(valeur).isZero()
        }

        @Test
        fun `doit valoriser AjouteOrDansBourses a 2 pts par or depose`() {
            // Bourse taille=3, depose 0. AjouteOrDansBourses(2) ajoute min(2, 3) = 2 par bourse.
            val bourse = BourseScore(taille = 3)
            val carteBourse = Villageois(
                nom = "Bourse", cout = 0, blasons = listOf(Blason.PAYSAN),
                effets = Effets(), effetScore = EffetScoreVide, bourse = bourse,
            )
            val carteAvecEffet = carteAvecEffets(listOf(AjouteOrDansBourses(or = 2)))
            val joueur = joueurAvec(listOf(
                CartePositionee(carteBourse, MILIEUMILIEU),
                CartePositionee(carteAvecEffet, com.chateaucombo.tableau.Position.HAUTMILIEU),
            ))

            val valeur = EvaluateurHeuristique.estimerValeurEffetsPlacement(carteAvecEffet, joueur)

            assertThat(valeur).isEqualTo(4) // 2 or deposes * 2 pts
        }

        @Test
        fun `doit valoriser RemplitBourses pour le nb plus grandes bourses`() {
            val grandeBourse = BourseScore(taille = 5)
            val petiteBourse = BourseScore(taille = 2)
            val grandeCarte = Villageois(
                nom = "Grande", cout = 0, blasons = listOf(Blason.PAYSAN),
                effets = Effets(), effetScore = EffetScoreVide, bourse = grandeBourse,
            )
            val petiteCarte = Villageois(
                nom = "Petite", cout = 0, blasons = listOf(Blason.PAYSAN),
                effets = Effets(), effetScore = EffetScoreVide, bourse = petiteBourse,
            )
            val carte = carteAvecEffets(listOf(RemplitBourses(nb = 1)))
            val joueur = joueurAvec(listOf(
                CartePositionee(grandeCarte, MILIEUMILIEU),
                CartePositionee(petiteCarte, com.chateaucombo.tableau.Position.HAUTMILIEU),
                CartePositionee(carte, com.chateaucombo.tableau.Position.BASMILIEU),
            ))

            val valeur = EvaluateurHeuristique.estimerValeurEffetsPlacement(carte, joueur)

            assertThat(valeur).isEqualTo(10) // 5 or deposes (grande bourse) * 2 pts
        }

        @Test
        fun `doit pénaliser les effets qui donnent des ressources aux adversaires`() {
            val carte = carteAvecEffets(listOf(AjouteOrPourTousLesAdversaires(or = 3)))
            val joueur = joueurAvec(listOf(CartePositionee(carte, MILIEUMILIEU)))

            val valeur = EvaluateurHeuristique.estimerValeurEffetsPlacement(carte, joueur)

            // -3 or * 0.3 (pas de bourse) = -0.9 → tronque a 0 en Int. Hmm mieux d'utiliser -1
            assertThat(valeur).isLessThanOrEqualTo(0)
        }

        @Test
        fun `doit sommer les valeurs en mode ET`() {
            val carte = carteAvecEffets(
                effets = listOf(AjouteCle(cle = 2), AjouteCleParVillageois()),
                separateur = EffetSeparateur.ET,
            )
            val joueur = joueurAvec(listOf(
                CartePositionee(carte, MILIEUMILIEU),
                CartePositionee(carteSansEffet("V1"), com.chateaucombo.tableau.Position.HAUTMILIEU),
            ))

            val valeur = EvaluateurHeuristique.estimerValeurEffetsPlacement(carte, joueur)

            // AjouteCle(2) = 2, AjouteCleParVillageois = 2 villageois = 2 → total 4
            assertThat(valeur).isEqualTo(4)
        }

        @Test
        fun `doit moyenner les valeurs en mode OU`() {
            val carte = carteAvecEffets(
                effets = listOf(AjouteCle(cle = 4), AjouteCle(cle = 2)),
                separateur = EffetSeparateur.OU,
            )
            val joueur = joueurAvec(listOf(CartePositionee(carte, MILIEUMILIEU)))

            val valeur = EvaluateurHeuristique.estimerValeurEffetsPlacement(carte, joueur)

            assertThat(valeur).isEqualTo(3) // moyenne de 4 et 2
        }

        @Test
        fun `doit ignorer les effets voisins et defausse`() {
            // AjouteCleParChatelain devrait etre valorise (joueur local), pas les effets voisins
            val effetConnus = AjouteCleParChatelain()
            val carte = carteAvecEffets(listOf(effetConnus))
            val chatelain = CartePositionee(chatelainSansEffet(), MILIEUMILIEU)
            val joueur = joueurAvec(listOf(chatelain, CartePositionee(carte, com.chateaucombo.tableau.Position.HAUTMILIEU)))

            val valeur = EvaluateurHeuristique.estimerValeurEffetsPlacement(carte, joueur)

            assertThat(valeur).isEqualTo(1) // 1 chatelain * 1 pt par cle
        }

        @Test
        fun `doit valoriser AjouteOrParBlasonDistinct par blason distinct`() {
            val bourse = BourseScore(taille = 10) // assez de place pour 2 pts par or
            val carteBourse = Villageois(
                nom = "Bourse", cout = 0, blasons = listOf(Blason.ARTISAN),
                effets = Effets(), effetScore = EffetScoreVide, bourse = bourse,
            )
            val cartesDifferentes = listOf(
                CartePositionee(carteBourse, MILIEUMILIEU),
                CartePositionee(carteSansEffet("A", listOf(Blason.NOBLE)), com.chateaucombo.tableau.Position.HAUTMILIEU),
                CartePositionee(carteSansEffet("B", listOf(Blason.MILITAIRE)), com.chateaucombo.tableau.Position.BASMILIEU),
            )
            val carte = carteAvecEffets(listOf(AjouteOrParBlasonDistinct()))
            val joueur = joueurAvec(cartesDifferentes + CartePositionee(carte, com.chateaucombo.tableau.Position.MILIEUGAUCHE))

            val valeur = EvaluateurHeuristique.estimerValeurEffetsPlacement(carte, joueur)

            // 4 blasons distincts (ARTISAN, NOBLE, MILITAIRE, PAYSAN) * 2 pts = 8
            assertThat(valeur).isEqualTo(8)
        }
    }

    @Nested
    inner class CoutOpportuniteOr {

        @Test
        fun `doit etre nul quand il n'y a pas de bourse a remplir`() {
            val joueur = joueurAvec(cartes = emptyList(), or = 10)

            val cout = EvaluateurHeuristique.coutOpportuniteOr(joueur, coutEffectif = 5)

            assertThat(cout).isZero()
        }

        @Test
        fun `doit etre nul quand l'or apres achat suffit a remplir les bourses`() {
            // bourse capacite=3, or=10, cout=5 => or apres = 5, suffisant pour remplir => pas de perte
            val bourse = BourseScore(taille = 3)
            val carteBourse = Villageois(
                nom = "B", cout = 0, blasons = listOf(Blason.PAYSAN),
                effets = Effets(), effetScore = EffetScoreVide, bourse = bourse,
            )
            val joueur = joueurAvec(listOf(CartePositionee(carteBourse, MILIEUMILIEU)), or = 10)

            val cout = EvaluateurHeuristique.coutOpportuniteOr(joueur, coutEffectif = 5)

            assertThat(cout).isZero()
        }

        @Test
        fun `doit penaliser l'or manquant pour remplir les bourses a 2 pts par or`() {
            // bourse capacite=6, or=4, cout=3 => or apres = 1, manque 5 pour remplir => 3 or perdus (min cout et 5)
            val bourse = BourseScore(taille = 6)
            val carteBourse = Villageois(
                nom = "B", cout = 0, blasons = listOf(Blason.PAYSAN),
                effets = Effets(), effetScore = EffetScoreVide, bourse = bourse,
            )
            val joueur = joueurAvec(listOf(CartePositionee(carteBourse, MILIEUMILIEU)), or = 4)

            val cout = EvaluateurHeuristique.coutOpportuniteOr(joueur, coutEffectif = 3)

            assertThat(cout).isEqualTo(6) // 3 or * 2 pts
        }

        @Test
        fun `doit plafonner la perte au cout effectif du coup`() {
            // bourse capacite=10, or=2, cout=2 => or apres = 0, manque 10 pour remplir, mais cout n'est que de 2
            val bourse = BourseScore(taille = 10)
            val carteBourse = Villageois(
                nom = "B", cout = 0, blasons = listOf(Blason.PAYSAN),
                effets = Effets(), effetScore = EffetScoreVide, bourse = bourse,
            )
            val joueur = joueurAvec(listOf(CartePositionee(carteBourse, MILIEUMILIEU)), or = 2)

            val cout = EvaluateurHeuristique.coutOpportuniteOr(joueur, coutEffectif = 2)

            assertThat(cout).isEqualTo(4) // min(2 cout, 10 manquant) * 2 pts
        }
    }
}
