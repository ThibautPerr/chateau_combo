package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason
import com.chateaucombo.deck.model.Blason.ERUDIT
import com.chateaucombo.deck.model.Blason.RELIGIEUX
import com.chateaucombo.deck.model.CarteVerso
import com.chateaucombo.deck.model.Chatelain
import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.effet.model.*
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
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
                CartePositionee(carte = chatelain(), position = Position.HAUTGAUCHE),
                CartePositionee(carte = chatelain(), position = Position.HAUTMILIEU),
                CartePositionee(carte = chatelain(), position = Position.HAUTDROITE),
            )
        )

        @Test
        fun `ne doit pas compter les chatelains face verso`() {
            val orInitial = 2
            val tableauAvecTroisChatelains = tableauAvecTroisChatelains()
            val carteVerso = CartePositionee(carte = chatelainVerso(), position = Position.HAUTGAUCHE)
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
                CartePositionee(carte = villageois(), position = Position.HAUTGAUCHE),
                CartePositionee(carte = villageois(), position = Position.HAUTMILIEU),
                CartePositionee(carte = villageois(), position = Position.HAUTDROITE),
            )
        )

        @Test
        fun `ne doit pas compter les villageois face verso`() {
            val orInitial = 2
            val tableauAvecTroisChatelains = tableauAvecTroisVillageois()
            val carteVerso = CartePositionee(carte = villageoisVerso(), position = Position.HAUTGAUCHE)
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
                    CartePositionee(carte = villageois(blasons = listOf(blason)), position = Position.HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(blason)), position = Position.HAUTMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(blason)), position = Position.HAUTDROITE),
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
                        position = Position.HAUTGAUCHE
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(blason, blason)),
                        position = Position.HAUTMILIEU
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(blason, blason)),
                        position = Position.HAUTDROITE
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
                        position = Position.HAUTGAUCHE
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX)),
                        position = Position.HAUTMILIEU
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX)),
                        position = Position.HAUTDROITE
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX, ERUDIT)),
                        position = Position.MILIEUGAUCHE
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
                        position = Position.HAUTGAUCHE
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX, ERUDIT)),
                        position = Position.HAUTMILIEU
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX, ERUDIT)),
                        position = Position.HAUTDROITE
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(RELIGIEUX)),
                        position = Position.MILIEUGAUCHE
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
                        position = Position.HAUTGAUCHE
                    ),
                    CartePositionee(
                        carte = villageois(cout = cout),
                        position = Position.HAUTMILIEU
                    ),
                    CartePositionee(
                        carte = villageois(cout = cout),
                        position = Position.HAUTDROITE
                    ),
                    CartePositionee(
                        carte = villageois(cout = -1),
                        position = Position.MILIEUGAUCHE
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
                    CartePositionee(carte = villageois(blasons = listOf(blason)), position = Position.HAUTGAUCHE),
                    CartePositionee(carte = villageois(blasons = listOf(blason)), position = Position.HAUTMILIEU),
                    CartePositionee(carte = villageois(blasons = listOf(blason)), position = Position.HAUTDROITE),
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
                        position = Position.HAUTGAUCHE
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(blason, blason)),
                        position = Position.HAUTMILIEU
                    ),
                    CartePositionee(
                        carte = villageois(blasons = listOf(blason, blason)),
                        position = Position.HAUTDROITE
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
                CartePositionee(carte = chatelain(), position = Position.HAUTGAUCHE),
                CartePositionee(carte = chatelain(), position = Position.HAUTMILIEU),
                CartePositionee(carte = chatelain(), position = Position.HAUTDROITE),
            )
        )

        @Test
        fun `ne doit pas compter les chatelains face verso`() {
            val cleInitial = 2
            val tableauAvecTroisChatelains = tableauAvecTroisChatelains()
            val carteVerso = CartePositionee(carte = chatelainVerso(), position = Position.HAUTGAUCHE)
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
                CartePositionee(carte = villageois(), position = Position.HAUTGAUCHE),
                CartePositionee(carte = villageois(), position = Position.HAUTMILIEU),
                CartePositionee(carte = villageois(), position = Position.HAUTDROITE),
            )
        )

        @Test
        fun `ne doit pas compter les villageois face verso`() {
            val cleInitial = 2
            val tableauAvecTroisChatelains = tableauAvecTroisVillageois()
            val carteVerso = CartePositionee(carte = villageoisVerso(), position = Position.HAUTGAUCHE)
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
}