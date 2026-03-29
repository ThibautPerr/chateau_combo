package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason
import com.chateaucombo.deck.model.Blason.*
import com.chateaucombo.deck.model.CarteVerso
import com.chateaucombo.deck.model.Chatelain
import com.chateaucombo.deck.model.Deck
import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.effet.model.*
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.model.Position.*
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource

class EffetTest {
    private fun villageois(
        cout: Int = 0,
        blasons: List<Blason> = emptyList(),
        effets: Effets = Effets(),
        effetScore: EffetScore = EffetScoreVide,
        bourse: BourseScore? = null
    ) =
        Villageois(
            cout = cout,
            nom = "carte",
            blasons = blasons,
            effets = effets,
            effetScore = effetScore,
            bourse = bourse
        )

    private fun chatelain(effets: Effets = Effets()) =
        Chatelain(
            cout = 0,
            nom = "carte",
            blasons = emptyList(),
            effets = effets
        )

    @Nested
    inner class AjouteCleEffet {
        @ParameterizedTest
        @ValueSource(ints = [1, 2, 3, 4])
        fun `doit ajouter autant de cles au joueur actuel`(cle: Int) {
            val cleInitiale = 2
            val joueur = Joueur(id = 1, cle = cleInitiale)
            val carte = villageois(effets = Effets(effets = listOf(AjouteCle(cle))))
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitiale + cle)
        }
    }

    @Nested
    inner class AjouteCleParBlasonDistinctEffet {
        @Test
        fun `doit ajouter autant de cles que de types de blasons distincts sur le tableau`() {
            val cleInitiale = 2
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, NOBLE)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(RELIGIEUX)), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTDROITE),
                )
            )
            val joueur = Joueur(id = 1, cle = cleInitiale, tableau = tableau)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDistinct())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitiale + 3) // MILITAIRE, NOBLE, RELIGIEUX
        }

        @Test
        fun `ne doit compter les blasons en double qu'une seule fois`() {
            val cleInitiale = 2
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, MILITAIRE)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTMILIEU),
                )
            )
            val joueur = Joueur(id = 1, cle = cleInitiale, tableau = tableau)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDistinct())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitiale + 1) // only MILITAIRE
        }

        @Test
        fun `ne doit pas ajouter de cles si le tableau est vide`() {
            val cleInitiale = 2
            val joueur = Joueur(id = 1, cle = cleInitiale)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDistinct())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitiale)
        }
    }

    @Nested
    inner class AjouteCleParBlasonAbsentEffet {
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

    @Nested
    inner class AjouteCleParBlasonDansTableauVoisinEffet {
        @Test
        fun `doit ajouter autant de cles que d'occurrences du blason dans le tableau du voisin`() {
            val cleInitiale = 2
            val tableauVoisin = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, NOBLE)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTMILIEU),
                )
            )
            val joueurActuel = Joueur(id = 0, cle = cleInitiale)
            val voisin = Joueur(id = 1, tableau = tableauVoisin)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDansTableauVoisin(MILITAIRE))))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel, voisin), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.cle).isEqualTo(cleInitiale + 2)
        }

        @Test
        fun `ne doit pas modifier le tableau du voisin`() {
            val cleVoisin = 5
            val tableauVoisin = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTGAUCHE),
                )
            )
            val joueurActuel = Joueur(id = 0)
            val voisin = Joueur(id = 1, cle = cleVoisin, tableau = tableauVoisin)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDansTableauVoisin(MILITAIRE))))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel, voisin), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(voisin.cle).isEqualTo(cleVoisin)
        }

        @Test
        fun `ne doit pas ajouter de cles si le joueur actuel est seul`() {
            val cleInitiale = 2
            val joueurActuel = Joueur(id = 0, cle = cleInitiale)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDansTableauVoisin(MILITAIRE))))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.cle).isEqualTo(cleInitiale)
        }

        @Test
        fun `doit choisir le voisin qui donne le plus de cles`() {
            val cleInitiale = 2
            val tableauPauvre = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTGAUCHE),
                )
            )
            val tableauRiche = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTDROITE),
                )
            )
            val joueurActuel = Joueur(id = 1, cle = cleInitiale)
            val voisinPauvre = Joueur(id = 0, tableau = tableauPauvre)
            val voisinRiche = Joueur(id = 2, tableau = tableauRiche)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDansTableauVoisin(MILITAIRE))))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisinPauvre, joueurActuel, voisinRiche), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.cle).isEqualTo(cleInitiale + 3)
        }
    }

    @Nested
    inner class AjouteOrParBlasonDansTableauVoisinEffet {
        @Test
        fun `doit ajouter autant d'or que de blasons du type indique dans le tableau du voisin`() {
            val orInitial = 2
            val tableauVoisin = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT, NOBLE)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTMILIEU),
                )
            )
            val joueurActuel = Joueur(id = 0, or = orInitial)
            val voisin = Joueur(id = 1, tableau = tableauVoisin)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDansTableauVoisin(ERUDIT))))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel, voisin), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.or).isEqualTo(orInitial + 2)
        }

        @Test
        fun `ne doit pas modifier le tableau du voisin`() {
            val orVoisin = 5
            val tableauVoisin = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
                )
            )
            val joueurActuel = Joueur(id = 0)
            val voisin = Joueur(id = 1, or = orVoisin, tableau = tableauVoisin)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDansTableauVoisin(ERUDIT))))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel, voisin), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(voisin.or).isEqualTo(orVoisin)
        }

        @Test
        fun `ne doit pas ajouter d'or si le joueur actuel est seul`() {
            val orInitial = 2
            val joueurActuel = Joueur(id = 0, or = orInitial)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDansTableauVoisin(ERUDIT))))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.or).isEqualTo(orInitial)
        }

        @Test
        fun `le premier et le dernier joueur sont voisins`() {
            val orInitial = 2
            val tableauVoisin = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
                )
            )
            val premierJoueur = Joueur(id = 0, tableau = tableauVoisin)
            val dernierJoueur = Joueur(id = 2, or = orInitial)
            val autreJoueur = Joueur(id = 1)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDansTableauVoisin(ERUDIT))))
            val context = EffetContext(joueurActuel = dernierJoueur, joueurs = listOf(premierJoueur, autreJoueur, dernierJoueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(dernierJoueur.or).isEqualTo(orInitial + 1)
        }

        @Test
        fun `doit choisir le voisin qui donne le plus d'or`() {
            val orInitial = 2
            val tableauPauvre = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
                )
            )
            val tableauRiche = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTDROITE),
                )
            )
            val joueurActuel = Joueur(id = 1, or = orInitial)
            val voisinPauvre = Joueur(id = 0, tableau = tableauPauvre)
            val voisinRiche = Joueur(id = 2, tableau = tableauRiche)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDansTableauVoisin(ERUDIT))))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisinPauvre, joueurActuel, voisinRiche), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.or).isEqualTo(orInitial + 3)
        }
    }

    @Nested
    inner class AjouteOrParBlasonDistinctEffet {
        @Test
        fun `doit ajouter autant d'or que de types de blasons distincts sur le tableau`() {
            val orInitial = 2
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, NOBLE)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(RELIGIEUX)), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTDROITE),
                )
            )
            val joueur = Joueur(id = 1, or = orInitial, tableau = tableau)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDistinct())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 3) // MILITAIRE, NOBLE, RELIGIEUX
        }

        @Test
        fun `ne doit compter les blasons en double qu'une seule fois`() {
            val orInitial = 2
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, MILITAIRE)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTMILIEU),
                )
            )
            val joueur = Joueur(id = 1, or = orInitial, tableau = tableau)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDistinct())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 1) // only MILITAIRE
        }

        @Test
        fun `ne doit pas ajouter d'or si le tableau est vide`() {
            val orInitial = 2
            val joueur = Joueur(id = 1, or = orInitial)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDistinct())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial)
        }
    }

    @Nested
    inner class AjouteOrParEmplacementVideEffet {
        @Test
        fun `doit ajouter autant d'or que d'emplacements vides sur le tableau`() {
            val orInitial = 2
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(), position = HAUTMILIEU),
                    CartePositionee(carte = chatelain(), position = HAUTDROITE),
                )
            )
            val joueur = Joueur(id = 1, or = orInitial, tableau = tableau)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParEmplacementVide())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 6) // 9 - 3 = 6 emplacements vides
        }

        @Test
        fun `doit ajouter neuf or si le tableau est vide`() {
            val orInitial = 2
            val joueur = Joueur(id = 1, or = orInitial)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParEmplacementVide())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 9)
        }
    }

    @Nested
    inner class AjouteOrParCartePositioneeEffet {
        @Test
        fun `doit ajouter autant d'or que de cartes positionnees sur le tableau`() {
            val orInitial = 2
            val tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(), position = HAUTMILIEU),
                    CartePositionee(carte = chatelain(), position = HAUTDROITE),
                )
            )
            val joueur = Joueur(id = 1, or = orInitial, tableau = tableau)
            val carte = villageois(effets = Effets(effets = listOf(AjouteOrParCartePositionee())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 3)
        }

        @Test
        fun `ne doit pas ajouter d'or si le tableau est vide`() {
            val orInitial = 2
            val joueur = Joueur(id = 1, or = orInitial)
            val carte = villageois(effets = Effets(effets = listOf(AjouteOrParCartePositionee())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial)
        }
    }

    @Nested
    inner class AjouteOrPourTousLesAdversairesEffet {
        @Test
        fun `doit ajouter de l'or a tous les adversaires`() {
            val orInitial = 2
            val joueurs = List(4) { Joueur(id = it, or = orInitial) }
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrPourTousLesAdversaires(2))))
            val context = EffetContext(
                joueurActuel = joueurs.first(),
                joueurs = joueurs,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            joueurs
                .filter { it.id != joueurs.first().id }
                .forEach { joueur ->
                    assertThat(joueur.or).isEqualTo(orInitial + 2)
                }
        }

        @Test
        fun `ne doit pas ajouter d'or au joueur actuel`() {
            val orInitial = 2
            val joueurs = List(4) { Joueur(id = it, or = orInitial) }
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrPourTousLesAdversaires(2))))
            val context = EffetContext(
                joueurActuel = joueurs.first(),
                joueurs = joueurs,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueurs.first().or).isEqualTo(orInitial)
        }

        @Test
        fun `doit ne rien faire s'il n'y a pas d'adversaires`() {
            val orInitial = 2
            val joueurActuel = Joueur(id = 0, or = orInitial)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrPourTousLesAdversaires(2))))
            val context = EffetContext(
                joueurActuel = joueurActuel,
                joueurs = listOf(joueurActuel),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.or).isEqualTo(orInitial)
        }
    }

    @Nested
    inner class AjouteClePourTousLesAdversairesEffet {
        @Test
        fun `doit ajouter une cle a tous les adversaires mais pas au joueur actuel`() {
            val cleInitiale = 2
            val joueurs = List(4) { Joueur(id = it, cle = cleInitiale) }
            val carte = villageois(effets = Effets(effets = listOf(AjouteClePourTousLesAdversaires(1))))
            val context = EffetContext(
                joueurActuel = joueurs.first(),
                joueurs = joueurs,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueurs.first().cle).isEqualTo(cleInitiale)
            joueurs
                .filter { it.id != joueurs.first().id }
                .forEach { joueur ->
                    assertThat(joueur.cle).isEqualTo(cleInitiale + 1)
                }
        }

        @Test
        fun `doit ne rien faire s'il n'y a pas d'adversaires`() {
            val cleInitiale = 2
            val joueurActuel = Joueur(id = 0, cle = cleInitiale)
            val carte = villageois(effets = Effets(effets = listOf(AjouteClePourTousLesAdversaires(1))))
            val context = EffetContext(
                joueurActuel = joueurActuel,
                joueurs = listOf(joueurActuel),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.cle).isEqualTo(cleInitiale)
        }
    }

    @Nested
    inner class AjouteClePourTousLesJoueursEffet {
        @Test
        fun `doit ajouter une cle a tous les joueurs`() {
            val cleInitiale = 2
            val joueurs = List(4) { Joueur(id = it, cle = cleInitiale) }
            val carte = villageois(effets = Effets(effets = listOf(AjouteClePourTousLesJoueurs(1))))
            val context = EffetContext(
                joueurActuel = joueurs.first(),
                joueurs = joueurs,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            joueurs.forEach { joueur ->
                assertThat(joueur.cle).isEqualTo(cleInitiale + 1)
            }
        }

        @Test
        fun `doit ajouter une cle au joueur actuel contrairement a AjouteClePourTousLesAdversaires`() {
            val cleInitiale = 2
            val joueurActuel = Joueur(id = 0, cle = cleInitiale)
            val carte = villageois(effets = Effets(effets = listOf(AjouteClePourTousLesJoueurs(1))))
            val context = EffetContext(
                joueurActuel = joueurActuel,
                joueurs = listOf(joueurActuel),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.cle).isEqualTo(cleInitiale + 1)
        }
    }

    @Nested
    inner class AjouteOrParChatelainEffet {
        @Test
        fun `doit ajouter autant d'or que de chatelains sur le tableau du joueur`() {
            val orInitial = 2
            val tableauAvecTroisChatelains = tableauAvecTroisChatelains()
            val joueur = Joueur(id = 1, or = orInitial, tableau = tableauAvecTroisChatelains)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParChatelain())))
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 3)
        }

        private fun tableauAvecTroisChatelains() = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
                CartePositionee(carte = chatelain(), position = HAUTMILIEU),
                CartePositionee(carte = chatelain(), position = HAUTDROITE),
            )
        )

        @Test
        fun `ne doit pas compter les chatelains face verso`() {
            val orInitial = 2
            val tableauAvecTroisChatelains = tableauAvecTroisChatelains()
            val carteVerso = CartePositionee(carte = chatelainVerso(), position = HAUTGAUCHE)
            tableauAvecTroisChatelains.cartesPositionees.add(carteVerso)
            val joueur = Joueur(id = 1, or = orInitial, tableau = tableauAvecTroisChatelains)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParChatelain())))
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 3)
        }

        private fun chatelainVerso() = CarteVerso(carteOriginale = chatelain())
    }

    @Nested
    inner class AjouteOrParVillageoisEffet {
        @Test
        fun `doit ajouter autant d'or que de villageois sur le tableau du joueur`() {
            val orInitial = 2
            val tableauAvecTroisVillageois = tableauAvecTroisVillageois()
            val joueur = Joueur(id = 1, or = orInitial, tableau = tableauAvecTroisVillageois)
            val carte = villageois(effets = Effets(effets = listOf(AjouteOrParVillageois())))
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 3)
        }

        private fun tableauAvecTroisVillageois() = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(), position = HAUTMILIEU),
                CartePositionee(carte = villageois(), position = HAUTDROITE),
            )
        )

        @Test
        fun `ne doit pas compter les villageois face verso`() {
            val orInitial = 2
            val tableauAvecTroisChatelains = tableauAvecTroisVillageois()
            val carteVerso = CartePositionee(carte = villageoisVerso(), position = HAUTGAUCHE)
            tableauAvecTroisChatelains.cartesPositionees.add(carteVerso)
            val joueur = Joueur(id = 1, or = orInitial, tableau = tableauAvecTroisChatelains)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParVillageois())))
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 3)
        }

        private fun villageoisVerso() = CarteVerso(carteOriginale = villageois())
    }

    @Nested
    inner class AjouteOrParBlasonEffet {
        @ParameterizedTest
        @CsvSource(
            value = [
                "1,NOBLE",
                "2,NOBLE",
                "1,RELIGIEUX",
                "2,RELIGIEUX",
                "1,ERUDIT",
                "2,ERUDIT",
                "1,MILITAIRE",
                "2,MILITAIRE",
                "1,ARTISAN",
                "2,ARTISAN",
                "1,PAYSAN",
                "2,PAYSAN"
            ]
        )
        fun `doit ajouter autant d'or par carte avec le blason dans les cartes positionnees`(
            orParBlason: Int,
            blason: Blason
        ) {
            val orInitial = 2
            val tableau = tableauAvecTroisCartesAvecLeBlason(blason)
            val joueur = Joueur(id = 1, or = orInitial, tableau = tableau)
            val carte =
                villageois(
                    effets = Effets(
                        effets = listOf(
                            AjouteOrPourChaqueBlason(
                                orParBlason = orParBlason,
                                blason = blason
                            )
                        )
                    )
                )
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 3 * orParBlason)
        }

        private fun tableauAvecTroisCartesAvecLeBlason(blason: Blason): Tableau =
            Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(blason)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(blason)), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(blason)), position = HAUTDROITE),
                )
            )

        @ParameterizedTest
        @CsvSource(
            value = [
                "1,NOBLE",
                "2,NOBLE",
                "1,RELIGIEUX",
                "2,RELIGIEUX",
                "1,ERUDIT",
                "2,ERUDIT",
                "1,MILITAIRE",
                "2,MILITAIRE",
                "1,ARTISAN",
                "2,ARTISAN",
                "1,PAYSAN",
                "2,PAYSAN"
            ]
        )
        fun `doit compter deux fois les cartes avec deux blasons identiques`(orParBlason: Int, blason: Blason) {
            val orInitial = 2
            val tableau = tableauAvecTroisCartesAvecDoubleBlason(blason)
            val joueur = Joueur(id = 1, or = orInitial, tableau = tableau)
            val carte = villageois(
                effets = Effets(
                    effets = listOf(
                        AjouteOrPourChaqueBlason(
                            orParBlason = orParBlason,
                            blason = blason
                        )
                    )
                )
            )
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 3 * 2 * orParBlason)
        }

        private fun tableauAvecTroisCartesAvecDoubleBlason(blason: Blason): Tableau =
            Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(
                        carte = villageois(blasons = listOf(blason, blason)),
                        position = HAUTGAUCHE
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(blason, blason)),
                        position = HAUTMILIEU
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(blason, blason)),
                        position = HAUTDROITE
                    ),
                )
            )

    }

    @Nested
    inner class AjouteCleParCarteAvecNbBlasonEffet {
        @Test
        fun `doit ajouter autant de cles que de cartes avec un seul blason`() {
            val cleInitial = 2
            val tableau = tableauAvecTroisCartesAvecUnSeulBlason()
            val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableau)
            val carte =
                villageois(
                    effets = Effets(
                        effets = listOf(
                            AjouteCleParCarteAvecNbBlason(nbBlason = 1)
                        )
                    )
                )
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitial + 3)
        }

        private fun tableauAvecTroisCartesAvecUnSeulBlason() =
            Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX)),
                        position = HAUTGAUCHE
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX)),
                        position = HAUTMILIEU
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX)),
                        position = HAUTDROITE
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX, ERUDIT)),
                        position = MILIEUGAUCHE
                    ),
                )
            )
        @Test
        fun `doit ajouter autant de cles que de cartes avec deux blason`() {
            val cleInitial = 2
            val tableau = tableauAvecTroisCartesAvecDeuxBlasons()
            val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableau)
            val carte =
                villageois(
                    effets = Effets(
                        effets = listOf(
                            AjouteCleParCarteAvecNbBlason(nbBlason = 2)
                        )
                    )
                )
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitial + 3)
        }

        private fun tableauAvecTroisCartesAvecDeuxBlasons() =
            Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX, ERUDIT)),
                        position = HAUTGAUCHE
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX, ERUDIT)),
                        position = HAUTMILIEU
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX, ERUDIT)),
                        position = HAUTDROITE
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX)),
                        position = MILIEUGAUCHE
                    ),
                )
            )
    }

    @Nested
    inner class AjouteOrParCarteAvecLeCoutEffet {
        @ParameterizedTest
        @CsvSource(
            "1,0",
            "3,0",
            "1,4",
            "3,4",
        )
        fun `doit ajouter autant de cles que de cartes avec un seul blason`(or: Int, cout: Int) {
            val orInitial = 2
            val tableau = tableauAvecTroisCartesAvecLeCout(cout)
            val joueur = Joueur(id = 1, or = orInitial, tableau = tableau)
            val carte = villageois(
                effets = Effets(
                    effets = listOf(
                        AjouteOrParCarteAvecLeCout(orParCarte = or, cout = cout)
                    )
                )
            )
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + or * 3)
        }

        private fun tableauAvecTroisCartesAvecLeCout(cout: Int) =
            Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(
                        carte = villageois(cout = cout),
                        position = HAUTGAUCHE
                    ),
                    CartePositionee(
                        carte = villageois(cout = cout),
                        position = HAUTMILIEU
                    ),
                    CartePositionee(
                        carte = villageois(cout = cout),
                        position = HAUTDROITE
                    ),
                    CartePositionee(
                        carte = villageois(cout = -1),
                        position = MILIEUGAUCHE
                    ),
                )
            )

    }

    @Nested
    inner class AjouteCleParBlasonEffet {
        @ParameterizedTest
        @CsvSource(
            value = [
                "NOBLE",
                "RELIGIEUX",
                "ERUDIT",
                "MILITAIRE",
                "ARTISAN",
                "PAYSAN",
            ]
        )
        fun `doit ajouter autant de cles par carte avec le blason dans les cartes positionnees`(blason: Blason) {
            val cleInitial = 2
            val tableau = tableauAvecTroisCartesAvecLeBlason(blason)
            val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableau)
            val carte =
                villageois(
                    effets = Effets(
                        effets = listOf(
                            AjouteClePourChaqueBlason(blason = blason)
                        )
                    )
                )
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitial + 3)
        }

        private fun tableauAvecTroisCartesAvecLeBlason(blason: Blason): Tableau =
            Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(blason)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(blason)), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(blason)), position = HAUTDROITE),
                )
            )

        @ParameterizedTest
        @CsvSource(
            value = [
                "NOBLE",
                "RELIGIEUX",
                "ERUDIT",
                "MILITAIRE",
                "ARTISAN",
                "PAYSAN",
            ]
        )
        fun `doit compter deux fois les cartes avec deux blasons identiques`(blason: Blason) {
            val cleInitial = 2
            val tableau = tableauAvecTroisCartesAvecDoubleBlason(blason)
            val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableau)
            val carte = villageois(
                effets = Effets(
                    effets = listOf(
                        AjouteClePourChaqueBlason(blason = blason)
                    )
                )
            )
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitial + 3 * 2)
        }

        private fun tableauAvecTroisCartesAvecDoubleBlason(blason: Blason): Tableau =
            Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(
                        carte = villageois(blasons = listOf(blason, blason)),
                        position = HAUTGAUCHE
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(blason, blason)),
                        position = HAUTMILIEU
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(blason, blason)),
                        position = HAUTDROITE
                    ),
                )
            )

    }

    @Nested
    inner class AjouteCleParChatelainEffet {
        @Test
        fun `doit ajouter autant de cles que de chatelains sur le tableau du joueur`() {
            val cleInitial = 2
            val tableauAvecTroisChatelains = tableauAvecTroisChatelains()
            val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableauAvecTroisChatelains)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParChatelain())))
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitial + 3)
        }

        private fun tableauAvecTroisChatelains() = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
                CartePositionee(carte = chatelain(), position = HAUTMILIEU),
                CartePositionee(carte = chatelain(), position = HAUTDROITE),
            )
        )

        @Test
        fun `ne doit pas compter les chatelains face verso`() {
            val cleInitial = 2
            val tableauAvecTroisChatelains = tableauAvecTroisChatelains()
            val carteVerso = CartePositionee(carte = chatelainVerso(), position = HAUTGAUCHE)
            tableauAvecTroisChatelains.cartesPositionees.add(carteVerso)
            val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableauAvecTroisChatelains)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParChatelain())))
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitial + 3)
        }

        private fun chatelainVerso() = CarteVerso(carteOriginale = chatelain())
    }

    @Nested
    inner class AjouteCleParVillageoisEffet {
        @Test
        fun `doit ajouter autant de cles que de villageois sur le tableau du joueur`() {
            val cleInitial = 2
            val tableauAvecTroisVillageois = tableauAvecTroisVillageois()
            val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableauAvecTroisVillageois)
            val carte = villageois(effets = Effets(effets = listOf(AjouteCleParVillageois())))
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitial + 3)
        }

        private fun tableauAvecTroisVillageois() = Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(), position = HAUTMILIEU),
                CartePositionee(carte = villageois(), position = HAUTDROITE),
            )
        )

        @Test
        fun `ne doit pas compter les villageois face verso`() {
            val cleInitial = 2
            val tableauAvecTroisChatelains = tableauAvecTroisVillageois()
            val carteVerso = CartePositionee(carte = villageoisVerso(), position = HAUTGAUCHE)
            tableauAvecTroisChatelains.cartesPositionees.add(carteVerso)
            val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableauAvecTroisChatelains)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParVillageois())))
            val context = EffetContext(
                joueurActuel = joueur,
                joueurs = emptyList(),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitial + 3)
        }

        private fun villageoisVerso() = CarteVerso(carteOriginale = villageois())
    }

    @Nested
    inner class AjouteOrEnDefaussantUnVillageoisEffet {
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

    @Nested
    inner class AjouteCleEnDefaussantUnVillageoisEffet {
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

    @Nested
    inner class AjouteOrEnDefaussantUnChatelainEffet {
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

    @Nested
    inner class AjouteCleParChatelainDansTableauVoisinEffet {
        @Test
        fun `doit ajouter autant de cles que de chatelains dans le tableau du voisin`() {
            val cleInitiale = 2
            val tableauVoisin = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
                    CartePositionee(carte = chatelain(), position = HAUTMILIEU),
                    CartePositionee(carte = chatelain(), position = HAUTDROITE),
                )
            )
            val voisin = Joueur(id = 0, tableau = tableauVoisin)
            val joueurActuel = Joueur(id = 1, cle = cleInitiale)
            val carte = villageois(effets = Effets(effets = listOf(AjouteCleParChatelainDansTableauVoisin())))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisin, joueurActuel), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.cle).isEqualTo(cleInitiale + 3)
        }

        @Test
        fun `ne doit pas ajouter de cles si le voisin n'a aucun chatelain`() {
            val cleInitiale = 2
            val tableauVoisin = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(), position = HAUTGAUCHE),
                )
            )
            val voisin = Joueur(id = 0, tableau = tableauVoisin)
            val joueurActuel = Joueur(id = 1, cle = cleInitiale)
            val carte = villageois(effets = Effets(effets = listOf(AjouteCleParChatelainDansTableauVoisin())))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisin, joueurActuel), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.cle).isEqualTo(cleInitiale)
        }

        @Test
        fun `ne doit pas ajouter de cles s'il n'y a pas de voisin`() {
            val cleInitiale = 2
            val joueurActuel = Joueur(id = 1, cle = cleInitiale)
            val carte = villageois(effets = Effets(effets = listOf(AjouteCleParChatelainDansTableauVoisin())))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.cle).isEqualTo(cleInitiale)
        }

        @Test
        fun `doit choisir le voisin qui donne le plus de cles`() {
            val cleInitiale = 2
            val tableauPauvre = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
                )
            )
            val tableauRiche = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
                    CartePositionee(carte = chatelain(), position = HAUTMILIEU),
                    CartePositionee(carte = chatelain(), position = HAUTDROITE),
                )
            )
            val joueurActuel = Joueur(id = 1, cle = cleInitiale)
            val voisinPauvre = Joueur(id = 0, tableau = tableauPauvre)
            val voisinRiche = Joueur(id = 2, tableau = tableauRiche)
            val carte = villageois(effets = Effets(effets = listOf(AjouteCleParChatelainDansTableauVoisin())))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisinPauvre, joueurActuel, voisinRiche), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.cle).isEqualTo(cleInitiale + 3)
        }

        @Test
        fun `doit fonctionner en position circulaire (premier et dernier joueur sont voisins)`() {
            val cleInitiale = 2
            val tableauVoisin = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
                )
            )
            val premierJoueur = Joueur(id = 0, tableau = tableauVoisin)
            val dernierJoueur = Joueur(id = 2, cle = cleInitiale)
            val autreJoueur = Joueur(id = 1)
            val carte = villageois(effets = Effets(effets = listOf(AjouteCleParChatelainDansTableauVoisin())))
            val context = EffetContext(joueurActuel = dernierJoueur, joueurs = listOf(premierJoueur, autreJoueur, dernierJoueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU), decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(dernierJoueur.cle).isEqualTo(cleInitiale + 1)
        }
    }

    @Nested
    inner class RemplitBoursesEffet {
        @Test
        fun `doit deposer l'or dans la bourse sans modifier l'or du joueur`() {
            val bourse = BourseScore(taille = 5)
            val joueur = Joueur(id = 1, or = 3, tableau = Tableau(
                cartesPositionees = mutableListOf(CartePositionee(carte = villageois(bourse = bourse), position = HAUTGAUCHE))
            ))
            val carte = chatelain(effets = Effets(effets = listOf(RemplitBourses(nb = 2))))
            val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            carte.effets.effets.first().apply(context)

            assertThat(bourse.orDepose).isEqualTo(5)
            assertThat(joueur.or).isEqualTo(3)
        }

        @Test
        fun `doit remplir au maximum deux bourses en choisissant les plus grandes`() {
            val bourse4 = BourseScore(taille = 4)
            val bourse6 = BourseScore(taille = 6)
            val bourse3 = BourseScore(taille = 3)
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(bourse = bourse4), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(bourse = bourse6), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(bourse = bourse3), position = HAUTDROITE),
                )
            ))
            val carte = chatelain(effets = Effets(effets = listOf(RemplitBourses(nb = 2))))
            val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            carte.effets.effets.first().apply(context)

            assertThat(bourse6.orDepose).isEqualTo(6)
            assertThat(bourse4.orDepose).isEqualTo(4)
            assertThat(bourse3.orDepose).isEqualTo(0)
        }

        @Test
        fun `ne doit pas modifier les bourses si aucune carte avec bourse n'est dans le tableau`() {
            val joueur = Joueur(id = 1)
            val carte = chatelain(effets = Effets(effets = listOf(RemplitBourses(nb = 2))))
            val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(15)
        }
    }

    @Nested
    inner class AjouteOrDansBoursesEffet {
        @Test
        fun `doit ajouter deux ors dans chaque bourse ayant de la place`() {
            val bourse = BourseScore(taille = 5)
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(CartePositionee(carte = villageois(bourse = bourse), position = HAUTGAUCHE))
            ))
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrDansBourses(or = 2))))
            val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            carte.effets.effets.first().apply(context)

            assertThat(bourse.orDepose).isEqualTo(2)
        }

        @Test
        fun `doit ajouter dans chaque bourse ayant de la place`() {
            val bourse1 = BourseScore(taille = 5)
            val bourse2 = BourseScore(taille = 3)
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(bourse = bourse1), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(bourse = bourse2), position = HAUTMILIEU),
                )
            ))
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrDansBourses(or = 2))))
            val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            carte.effets.effets.first().apply(context)

            assertThat(bourse1.orDepose).isEqualTo(2)
            assertThat(bourse2.orDepose).isEqualTo(2)
        }

        @Test
        fun `ne doit pas depasser la taille de la bourse`() {
            val bourse = BourseScore(taille = 3)
            bourse.orDepose = 2
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(CartePositionee(carte = villageois(bourse = bourse), position = HAUTGAUCHE))
            ))
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrDansBourses(or = 2))))
            val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            carte.effets.effets.first().apply(context)

            assertThat(bourse.orDepose).isEqualTo(3)
        }

        @Test
        fun `ne doit pas ajouter d'or dans une bourse deja pleine`() {
            val bourse = BourseScore(taille = 3)
            bourse.orDepose = 3
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(CartePositionee(carte = villageois(bourse = bourse), position = HAUTGAUCHE))
            ))
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrDansBourses(or = 2))))
            val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            carte.effets.effets.first().apply(context)

            assertThat(bourse.orDepose).isEqualTo(3)
        }
    }

    @Nested
    inner class PointsParTripleBlasonEffet {
        @Test
        fun `doit donner des points par triple d'un meme blason`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE, NOBLE)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE, MILITAIRE, MILITAIRE)), position = HAUTDROITE),
                )
            ))
            val carte = villageois(effetScore = PointsParTripleBlason(points = 6))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(12)
        }

        @Test
        fun `doit compter les triples independamment par type de blason`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE, NOBLE, NOBLE)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT, ERUDIT, ERUDIT)), position = HAUTMILIEU),
                )
            ))
            val carte = villageois(effetScore = PointsParTripleBlason(points = 6))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(12)
        }

        @Test
        fun `doit retourner zero si aucun triple n'est present`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE, NOBLE)), position = HAUTGAUCHE),
                )
            ))
            val carte = villageois(effetScore = PointsParTripleBlason(points = 6))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsParBlasonDistinctDansLaRangeeEffet {
        @Test
        fun `doit compter les blasons distincts dans la meme rangee`() {
            val carte = villageois(blasons = listOf(MILITAIRE), effetScore = PointsParBlasonDistinctDansLaRangee(points = 2))
            val cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE, ERUDIT)), position = MILIEUGAUCHE),
                    cartePositionee,
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT, NOBLE)), position = MILIEUDROITE),
                    CartePositionee(carte = villageois(blasons = listOf(PAYSAN)), position = HAUTMILIEU),
                )
            ))
            val context = ScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

            assertThat(carte.effetScore.score(context)).isEqualTo(6)
        }

        @Test
        fun `ne doit pas compter les blasons des autres rangees`() {
            val carte = villageois(effetScore = PointsParBlasonDistinctDansLaRangee(points = 2))
            val cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    cartePositionee,
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = BASMILIEU),
                )
            ))
            val context = ScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }

        @Test
        fun `doit compter les blasons de la carte elle-meme`() {
            val carte = villageois(blasons = listOf(ARTISAN, PAYSAN), effetScore = PointsParBlasonDistinctDansLaRangee(points = 2))
            val cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(cartePositionee)
            ))
            val context = ScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

            assertThat(carte.effetScore.score(context)).isEqualTo(4)
        }
    }

    @Nested
    inner class PointsParBlasonDistinctDansLaColonneEffet {
        @Test
        fun `doit compter les blasons distincts dans la meme colonne`() {
            val carte = villageois(blasons = listOf(MILITAIRE), effetScore = PointsParBlasonDistinctDansLaColonne(points = 2))
            val cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE, ERUDIT)), position = HAUTMILIEU),
                    cartePositionee,
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT, NOBLE)), position = BASMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(PAYSAN)), position = MILIEUGAUCHE),
                )
            ))
            val context = ScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

            assertThat(carte.effetScore.score(context)).isEqualTo(6)
        }

        @Test
        fun `ne doit pas compter les blasons des autres colonnes`() {
            val carte = villageois(effetScore = PointsParBlasonDistinctDansLaColonne(points = 2))
            val cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    cartePositionee,
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = MILIEUGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = MILIEUDROITE),
                )
            ))
            val context = ScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }

        @Test
        fun `doit compter les blasons de la carte elle-meme`() {
            val carte = villageois(blasons = listOf(ARTISAN, PAYSAN), effetScore = PointsParBlasonDistinctDansLaColonne(points = 2))
            val cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(cartePositionee)
            ))
            val context = ScoreContext(joueurActuel = joueur, cartePositionee = cartePositionee)

            assertThat(carte.effetScore.score(context)).isEqualTo(4)
        }
    }

    @Nested
    inner class PointsParChatelainEffet {
        @ParameterizedTest
        @CsvSource("0, 0", "1, 2", "3, 6")
        fun `doit donner des points par chatelain sur le tableau`(nbChatelains: Int, pointsAttendus: Int) {
            val cartesPositionees = Position.entries.take(nbChatelains)
                .map { CartePositionee(carte = chatelain(), position = it) }
                .toMutableList()
            val joueur = Joueur(id = 1, tableau = Tableau(cartesPositionees = cartesPositionees))
            val carte = villageois(effetScore = PointsParChatelain(points = 2))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(pointsAttendus)
        }

        @Test
        fun `ne doit pas compter les villageois`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = chatelain(), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(), position = HAUTMILIEU),
                )
            ))
            val carte = villageois(effetScore = PointsParChatelain(points = 2))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(2)
        }
    }

    @Nested
    inner class PointsParVillageoisEffet {
        @ParameterizedTest
        @CsvSource("0, 0", "1, 1", "3, 3")
        fun `doit donner des points par villageois sur le tableau`(nbVillageois: Int, pointsAttendus: Int) {
            val cartesPositionees = Position.entries.take(nbVillageois)
                .map { CartePositionee(carte = villageois(), position = it) }
                .toMutableList()
            val joueur = Joueur(id = 1, tableau = Tableau(cartesPositionees = cartesPositionees))
            val carte = villageois(effetScore = PointsParVillageois(points = 1))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(pointsAttendus)
        }

        @Test
        fun `ne doit pas compter les chatelains`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(), position = HAUTGAUCHE),
                    CartePositionee(carte = chatelain(), position = HAUTMILIEU),
                )
            ))
            val carte = villageois(effetScore = PointsParVillageois(points = 1))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(1)
        }
    }

    @Nested
    inner class PointsParBlasonDistinctEffet {
        @Test
        fun `doit donner des points par blason distinct sur le tableau`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE, ERUDIT)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT, MILITAIRE)), position = HAUTMILIEU),
                )
            ))
            val carte = villageois(effetScore = PointsParBlasonDistinct(points = 2))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(6)
        }

        @Test
        fun `doit retourner zero si le tableau est vide`() {
            val joueur = Joueur(id = 1)
            val carte = villageois(effetScore = PointsParBlasonDistinct(points = 2))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsParTripleVillageoisEffet {
        @ParameterizedTest
        @CsvSource("0, 0", "1, 0", "2, 0", "3, 7", "5, 7", "6, 14", "9, 21")
        fun `doit donner des points par triple de villageois`(nbVillageois: Int, pointsAttendus: Int) {
            val cartesPositionees = Position.entries.take(nbVillageois)
                .map { CartePositionee(carte = villageois(), position = it) }
                .toMutableList()
            val joueur = Joueur(id = 1, tableau = Tableau(cartesPositionees = cartesPositionees))
            val carte = villageois(effetScore = PointsParTripleVillageois(points = 7))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(pointsAttendus)
        }
    }

    @Nested
    inner class PointsParBlasonDansLaColonneEffet {
        @Test
        fun `doit compter les blasons dans la meme colonne`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT, ERUDIT)), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = BASMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = MILIEUGAUCHE),
                )
            ))
            val carte = villageois(effetScore = PointsParBlasonDansLaColonne(points = 3, blason = ERUDIT))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(9)
        }

        @Test
        fun `ne doit pas compter les blasons des autres colonnes`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = MILIEUGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = MILIEUDROITE),
                )
            ))
            val carte = villageois(effetScore = PointsParBlasonDansLaColonne(points = 3, blason = ERUDIT))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }

        @Test
        fun `doit retourner zero si le blason est absent de la colonne`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTMILIEU),
                )
            ))
            val carte = villageois(effetScore = PointsParBlasonDansLaColonne(points = 3, blason = ERUDIT))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsParBlasonDansLaRangeeEffet {
        @Test
        fun `doit compter les blasons dans la meme rangee`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE, NOBLE)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTDROITE),
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = MILIEUGAUCHE),
                )
            ))
            val carte = villageois(effetScore = PointsParBlasonDansLaRangee(points = 3, blason = NOBLE))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = HAUTMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(9)
        }

        @Test
        fun `ne doit pas compter les blasons des autres rangees`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = MILIEUGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = BASMILIEU),
                )
            ))
            val carte = villageois(effetScore = PointsParBlasonDansLaRangee(points = 3, blason = NOBLE))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = HAUTMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }

        @Test
        fun `doit retourner zero si le blason est absent de la rangee`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
                )
            ))
            val carte = villageois(effetScore = PointsParBlasonDansLaRangee(points = 3, blason = NOBLE))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = HAUTMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsParCarteAvecCoutExactEffet {
        @Test
        fun `doit donner des points par carte dont le cout correspond exactement`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(cout = 0), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(cout = 0), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(cout = 3), position = HAUTDROITE),
                )
            ))
            val carte = villageois(effetScore = PointsParCarteAvecCoutExact(points = 2, cout = 0))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(4)
        }

        @Test
        fun `doit retourner zero si aucune carte n'a le cout exact`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(cout = 3), position = HAUTGAUCHE),
                )
            ))
            val carte = villageois(effetScore = PointsParCarteAvecCoutExact(points = 2, cout = 0))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsParCarteAvecNbBlasonMinimumEffet {
        @Test
        fun `doit donner des points par carte ayant au moins le nombre de blasons requis`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(ARTISAN, MILITAIRE)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE, NOBLE)), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTDROITE),
                )
            ))
            val carte = villageois(effetScore = PointsParCarteAvecNbBlasonMinimum(points = 2, nbBlasonMinimum = 2))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(4)
        }

        @Test
        fun `doit retourner zero si aucune carte n'a suffisamment de blasons`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
                )
            ))
            val carte = villageois(effetScore = PointsParCarteAvecNbBlasonMinimum(points = 2, nbBlasonMinimum = 2))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsSiCarteVersoPresenteEffet {
        @Test
        fun `doit ajouter les points si au moins une carte verso est dans le tableau`() {
            val carteOriginale = villageois()
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = CarteVerso(carteOriginale = carteOriginale), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(), position = HAUTMILIEU),
                )
            ))
            val carte = villageois(effetScore = PointsSiCarteVersoPresente(points = 8))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(8)
        }

        @Test
        fun `ne doit pas ajouter de points si aucune carte verso n'est dans le tableau`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(), position = HAUTGAUCHE),
                )
            ))
            val carte = villageois(effetScore = PointsSiCarteVersoPresente(points = 8))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsParCarteAvecCoutMinimumEffet {
        @Test
        fun `doit donner des points par carte dont le cout est superieur ou egal au minimum`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(cout = 5), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(cout = 6), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(cout = 4), position = HAUTDROITE),
                )
            ))
            val carte = villageois(effetScore = PointsParCarteAvecCoutMinimum(points = 5, coutMinimum = 5))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(10)
        }

        @Test
        fun `doit retourner zero si aucune carte n'atteint le cout minimum`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(cout = 3), position = HAUTGAUCHE),
                )
            ))
            val carte = villageois(effetScore = PointsParCarteAvecCoutMinimum(points = 5, coutMinimum = 5))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsParCarteAvecReductionDeCoutEffet {
        @Test
        fun `doit donner des points par carte avec reduction de cout chatelain ou villageois`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = chatelain(effets = Effets(effetsPassifs = listOf(ReduceCoutChatelain()))), position = HAUTGAUCHE),
                    CartePositionee(carte = chatelain(effets = Effets(effetsPassifs = listOf(ReduceCoutVillageois()))), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(), position = HAUTDROITE),
                )
            ))
            val carte = villageois(effetScore = PointsParCarteAvecReductionDeCout(points = 4))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(8)
        }

        @Test
        fun `doit compter une carte avec les deux reductions`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = chatelain(effets = Effets(effetsPassifs = listOf(ReduceCoutChatelain(), ReduceCoutVillageois()))), position = HAUTGAUCHE),
                )
            ))
            val carte = villageois(effetScore = PointsParCarteAvecReductionDeCout(points = 4))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(4)
        }

        @Test
        fun `doit retourner zero si aucune carte n'a de reduction de cout`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(), position = HAUTGAUCHE),
                )
            ))
            val carte = villageois(effetScore = PointsParCarteAvecReductionDeCout(points = 4))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsParCleEffet {
        @ParameterizedTest
        @ValueSource(ints = [0, 1, 5, 13])
        fun `doit donner autant de points que de cles`(cle: Int) {
            val joueur = Joueur(id = 1, cle = cle)
            val carte = villageois(effetScore = PointsParCle())
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(cle)
        }
    }

    @Nested
    inner class PointsSiBlasonAbsentEffet {
        @Test
        fun `doit ajouter les points si aucune carte du tableau n'a le blason`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(ERUDIT)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(NOBLE)), position = HAUTMILIEU),
                )
            ))
            val carte = villageois(effetScore = PointsSiBlasonAbsent(points = 10, blason = MILITAIRE))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(10)
        }

        @Test
        fun `ne doit pas ajouter de points si une carte du tableau a le blason`() {
            val joueur = Joueur(id = 1, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(blasons = listOf(MILITAIRE)), position = HAUTGAUCHE),
                )
            ))
            val carte = villageois(effetScore = PointsSiBlasonAbsent(points = 10, blason = MILITAIRE))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }

        @Test
        fun `doit ajouter les points si le tableau est vide`() {
            val joueur = Joueur(id = 1)
            val carte = villageois(effetScore = PointsSiBlasonAbsent(points = 10, blason = MILITAIRE))
            val context = ScoreContext(
                joueurActuel = joueur,
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(10)
        }
    }

    @Nested
    inner class PointsSiBordEffet {
        @ParameterizedTest
        @EnumSource(names = ["HAUTMILIEU", "MILIEUGAUCHE", "MILIEUDROITE", "BASMILIEU"])
        fun `doit ajouter les points si la carte est en position de bord`(position: Position) {
            val carte = villageois(effetScore = PointsSiBord(points = 3))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = position)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(3)
        }

        @ParameterizedTest
        @EnumSource(names = ["HAUTGAUCHE", "HAUTDROITE", "MILIEUMILIEU", "BASGAUCHE", "BASDROITE"])
        fun `ne doit pas ajouter de points si la carte est au centre ou en coin`(position: Position) {
            val carte = villageois(effetScore = PointsSiBord(points = 3))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = position)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsSiCoinEffet {
        @ParameterizedTest
        @EnumSource(names = ["HAUTGAUCHE", "HAUTDROITE", "BASGAUCHE", "BASDROITE"])
        fun `doit ajouter les points si la carte est en position de coin`(position: Position) {
            val carte = villageois(effetScore = PointsSiCoin(points = 4))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = position)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(4)
        }

        @ParameterizedTest
        @EnumSource(names = ["HAUTMILIEU", "MILIEUGAUCHE", "MILIEUMILIEU", "MILIEUDROITE", "BASMILIEU"])
        fun `ne doit pas ajouter de points si la carte n'est pas en coin`(position: Position) {
            val carte = villageois(effetScore = PointsSiCoin(points = 4))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = position)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsSiColonneGaucheEffet {
        @Test
        fun `doit ajouter les points si la carte est dans la colonne gauche`() {
            val carte = villageois(effetScore = PointsSiColonneGauche(points = 6))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUGAUCHE)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(6)
        }

        @Test
        fun `doit ajouter les points meme si la carte n'est pas au centre du rang`() {
            val carte = villageois(effetScore = PointsSiColonneGauche(points = 6))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = HAUTGAUCHE)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(6)
        }

        @Test
        fun `ne doit pas ajouter de points si la carte n'est pas dans la colonne gauche`() {
            val carte = villageois(effetScore = PointsSiColonneGauche(points = 6))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsSiColonneMilieuEffet {
        @Test
        fun `doit ajouter les points si la carte est dans la colonne milieu`() {
            val carte = villageois(effetScore = PointsSiColonneMilieu(points = 6))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(6)
        }

        @Test
        fun `doit ajouter les points meme si la carte n'est pas au centre du rang`() {
            val carte = villageois(effetScore = PointsSiColonneMilieu(points = 6))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = HAUTMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(6)
        }

        @Test
        fun `ne doit pas ajouter de points si la carte n'est pas dans la colonne milieu`() {
            val carte = villageois(effetScore = PointsSiColonneMilieu(points = 6))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUGAUCHE)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsSiColonneDroiteEffet {
        @Test
        fun `doit ajouter les points si la carte est dans la colonne droite`() {
            val carte = villageois(effetScore = PointsSiColonneDroite(points = 5))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUDROITE)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(5)
        }

        @Test
        fun `doit ajouter les points meme si la carte n'est pas au centre du rang`() {
            val carte = villageois(effetScore = PointsSiColonneDroite(points = 5))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = HAUTDROITE)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(5)
        }

        @Test
        fun `ne doit pas ajouter de points si la carte n'est pas dans la colonne droite`() {
            val carte = villageois(effetScore = PointsSiColonneDroite(points = 5))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsSiRangInferieurEffet {
        @Test
        fun `doit ajouter les points si la carte est dans le rang inferieur`() {
            val carte = villageois(effetScore = PointsSiRangInferieur(points = 7))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = BASMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(7)
        }

        @Test
        fun `doit ajouter les points meme si la carte n'est pas au centre du rang`() {
            val carte = villageois(effetScore = PointsSiRangInferieur(points = 7))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = BASGAUCHE)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(7)
        }

        @Test
        fun `ne doit pas ajouter de points si la carte n'est pas dans le rang inferieur`() {
            val carte = villageois(effetScore = PointsSiRangInferieur(points = 7))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class PointsSiRangMilieuEffet {
        @Test
        fun `doit ajouter les points si la carte est dans le rang milieu vertical`() {
            val carte = villageois(effetScore = PointsSiRangMilieu(points = 5))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(5)
        }

        @Test
        fun `doit ajouter les points meme si la carte n'est pas au centre`() {
            val carte = villageois(effetScore = PointsSiRangMilieu(points = 5))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = MILIEUGAUCHE)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(5)
        }

        @Test
        fun `ne doit pas ajouter de points si la carte n'est pas dans le rang milieu vertical`() {
            val carte = villageois(effetScore = PointsSiRangMilieu(points = 5))
            val context = ScoreContext(
                joueurActuel = Joueur(id = 1),
                cartePositionee = CartePositionee(carte = carte, position = HAUTMILIEU)
            )

            assertThat(carte.effetScore.score(context)).isEqualTo(0)
        }
    }

    @Nested
    inner class AjouteCleParCarteBourseEffet {
        @Test
        fun `doit ajouter une cle par carte avec bourse dans le tableau`() {
            val cleInitiale = 2
            val joueur = Joueur(id = 1, cle = cleInitiale, tableau = Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(carte = villageois(bourse = BourseScore(taille = 5)), position = HAUTGAUCHE),
                    CartePositionee(carte = villageois(bourse = BourseScore(taille = 3)), position = HAUTMILIEU),
                    CartePositionee(carte = villageois(), position = HAUTDROITE),
                )
            ))
            val carte = villageois(effets = Effets(effets = listOf(AjouteCleParCarteBourse())))
            val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitiale + 2)
        }

        @Test
        fun `ne doit pas ajouter de cle si aucune carte avec bourse n'est dans le tableau`() {
            val cleInitiale = 2
            val joueur = Joueur(id = 1, cle = cleInitiale)
            val carte = villageois(effets = Effets(effets = listOf(AjouteCleParCarteBourse())))
            val context = EffetContext(joueurActuel = joueur, joueurs = listOf(joueur), cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU))

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitiale)
        }
    }
}