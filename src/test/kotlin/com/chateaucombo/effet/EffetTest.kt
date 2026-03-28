package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason
import com.chateaucombo.deck.model.Blason.*
import com.chateaucombo.deck.model.CarteVerso
import com.chateaucombo.deck.model.Chatelain
import com.chateaucombo.deck.model.Deck
import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.effet.model.*
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.*
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class EffetTest {
    private fun villageois(
        cout: Int = 0,
        blasons: List<Blason> = emptyList(),
        effets: Effets = Effets()
    ) =
        Villageois(
            cout = cout,
            nom = "carte",
            blasons = blasons,
            effets = effets
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
                carte = carte,
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
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitiale + 1) // only MILITAIRE
        }

        @Test
        fun `ne doit pas ajouter de cles si le tableau est vide`() {
            val cleInitiale = 2
            val joueur = Joueur(id = 1, cle = cleInitiale)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDistinct())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.cle).isEqualTo(cleInitiale + 5) // NOBLE, RELIGIEUX, ERUDIT, ARTISAN, PAYSAN absents
        }

        @Test
        fun `doit ajouter six cles si le tableau est vide`() {
            val cleInitiale = 2
            val joueur = Joueur(id = 1, cle = cleInitiale)
            val carte = villageois(effets = Effets(effets = listOf(AjouteCleParBlasonAbsent())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel, voisin), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel, voisin), carte = carte, decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(voisin.cle).isEqualTo(cleVoisin)
        }

        @Test
        fun `ne doit pas ajouter de cles si le joueur actuel est seul`() {
            val cleInitiale = 2
            val joueurActuel = Joueur(id = 0, cle = cleInitiale)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteCleParBlasonDansTableauVoisin(MILITAIRE))))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisinPauvre, joueurActuel, voisinRiche), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel, voisin), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel, voisin), carte = carte, decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(voisin.or).isEqualTo(orVoisin)
        }

        @Test
        fun `ne doit pas ajouter d'or si le joueur actuel est seul`() {
            val orInitial = 2
            val joueurActuel = Joueur(id = 0, or = orInitial)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDansTableauVoisin(ERUDIT))))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = dernierJoueur, joueurs = listOf(premierJoueur, autreJoueur, dernierJoueur), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisinPauvre, joueurActuel, voisinRiche), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 1) // only MILITAIRE
        }

        @Test
        fun `ne doit pas ajouter d'or si le tableau est vide`() {
            val orInitial = 2
            val joueur = Joueur(id = 1, or = orInitial)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParBlasonDistinct())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 6) // 9 - 3 = 6 emplacements vides
        }

        @Test
        fun `doit ajouter neuf or si le tableau est vide`() {
            val orInitial = 2
            val joueur = Joueur(id = 1, or = orInitial)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrParEmplacementVide())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 3)
        }

        @Test
        fun `ne doit pas ajouter d'or si le tableau est vide`() {
            val orInitial = 2
            val joueur = Joueur(id = 1, or = orInitial)
            val carte = villageois(effets = Effets(effets = listOf(AjouteOrParCartePositionee())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

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
                carte = carte,
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
                carte = carte,
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            assertThat(joueurs.first().or).isEqualTo(orInitial)
        }
    }

    @Nested
    inner class AjouteClePourTousLesAdversairesEffet {
        @Test
        fun `doit ajouter une cle a tous les joueurs`() {
            val cleInitiale = 2
            val joueurs = List(4) { Joueur(id = it, cle = cleInitiale) }
            val carte = villageois(effets = Effets(effets = listOf(AjouteClePourTousLesAdversaires(1))))
            val context = EffetContext(
                joueurActuel = joueurs.first(),
                joueurs = joueurs,
                carte = carte,
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
                carte = carte,
                decks = emptyList()
            )

            carte.effets.effets.first().apply(context)

            joueurs.forEach { joueur ->
                assertThat(joueur.cle).isEqualTo(cleInitiale + 1)
            }
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
                carte = carte,
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
                carte = carte,
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
                carte = carte,
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
                carte = carte,
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
                carte = carte,
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
                carte = carte,
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
                carte = carte,
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
                carte = carte,
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
                carte = carte,
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
                carte = carte,
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
                carte = carte,
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
                carte = carte,
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
                carte = carte,
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
                carte = carte,
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
                carte = carte,
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
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = listOf(deckVillageois))

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial + 5)
        }

        @Test
        fun `doit retirer la carte defaussee des cartesDisponibles et l'ajouter a la defausse`() {
            val joueur = Joueur(id = 1)
            val carteDisponible = villageois(cout = 3)
            val deckVillageois = Deck(nom = "Villageois", cartes = mutableListOf(), cartesDisponibles = mutableListOf(carteDisponible), estLeDeckActuel = true)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrEnDefaussantUnVillageois())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = listOf(deckVillageois))

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
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = listOf(deckVillageois))

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
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = listOf(deckVillageois))

            carte.effets.effets.first().apply(context)

            assertThat(joueur.or).isEqualTo(orInitial)
        }

        @Test
        fun `doit lever une erreur si le deck villageois est absent`() {
            val joueur = Joueur(id = 1)
            val carte = chatelain(effets = Effets(effets = listOf(AjouteOrEnDefaussantUnVillageois())))
            val context = EffetContext(joueurActuel = joueur, joueurs = emptyList(), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisin, joueurActuel), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisin, joueurActuel), carte = carte, decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(joueurActuel.cle).isEqualTo(cleInitiale)
        }

        @Test
        fun `ne doit pas ajouter de cles s'il n'y a pas de voisin`() {
            val cleInitiale = 2
            val joueurActuel = Joueur(id = 1, cle = cleInitiale)
            val carte = villageois(effets = Effets(effets = listOf(AjouteCleParChatelainDansTableauVoisin())))
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(joueurActuel), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = joueurActuel, joueurs = listOf(voisinPauvre, joueurActuel, voisinRiche), carte = carte, decks = emptyList())

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
            val context = EffetContext(joueurActuel = dernierJoueur, joueurs = listOf(premierJoueur, autreJoueur, dernierJoueur), carte = carte, decks = emptyList())

            carte.effets.effets.first().apply(context)

            assertThat(dernierJoueur.cle).isEqualTo(cleInitiale + 1)
        }
    }
}