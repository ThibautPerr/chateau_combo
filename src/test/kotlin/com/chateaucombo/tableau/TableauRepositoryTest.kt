package com.chateaucombo.tableau

import com.chateaucombo.deck.model.Blason
import com.chateaucombo.deck.model.Carte
import com.chateaucombo.deck.model.Chatelain
import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.model.Tableau
import com.chateaucombo.tableau.repository.TableauRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TableauRepositoryTest {
    private val repository = TableauRepository()

    @Nested
    inner class Ajout {

        @ParameterizedTest
        @CsvSource(
            value = [
                "HAUTGAUCHE",
                "HAUTMILIEU",
                "HAUTDROITE",
                "MILIEUGAUCHE",
                "MILIEUMILIEU",
                "MILIEUDROITE",
                "BASGAUCHE",
                "BASMILIEU",
                "BASDROITE",
            ]
        )
        fun `doit placer un chatelain dans le tableau`(position: Position) {
            val tableau = Tableau()
            val chatelain = Chatelain(
                nom = "Aumônier",
                cout = 5,
                blasons = listOf(Blason.RELIGIEUX)
            )

            repository.ajouteCarte(tableau, chatelain, position)

            assertThat(tableau.cartesPositionees).hasSize(1)
            assertThat(tableau.cartesPositionees.first().carte).isEqualTo(chatelain)
            assertThat(tableau.cartesPositionees.first().position).isEqualTo(position)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "HAUTGAUCHE",
                "HAUTMILIEU",
                "HAUTDROITE",
                "MILIEUGAUCHE",
                "MILIEUMILIEU",
                "MILIEUDROITE",
                "BASGAUCHE",
                "BASMILIEU",
                "BASDROITE",
            ]
        )
        fun `doit placer un villageois dans le tableau`(position: Position) {
            val tableau = Tableau()
            val villageois = villageois()

            repository.ajouteCarte(tableau, villageois, position)

            assertThat(tableau.cartesPositionees).hasSize(1)
            assertThat(tableau.cartesPositionees.first().carte).isEqualTo(villageois)
            assertThat(tableau.cartesPositionees.first().position).isEqualTo(position)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "HAUTGAUCHE",
                "HAUTMILIEU",
                "HAUTDROITE",
                "MILIEUGAUCHE",
                "MILIEUMILIEU",
                "MILIEUDROITE",
                "BASGAUCHE",
                "BASMILIEU",
                "BASDROITE",
            ]
        )
        fun `doit renvoyer faux et empecher l'ajout d'une carte sur une position deja occupee`(position: Position) {
            val tableau = Tableau()
            val villageois = villageois()

            val premierCoup = repository.ajouteCarte(tableau, villageois, position)
            val deuxiemeCoup = repository.ajouteCarte(tableau, villageois, position)

            assertThat(premierCoup).isTrue()
            assertThat(deuxiemeCoup).isFalse()
            assertThat(tableau.cartesPositionees.size).isEqualTo(1)
            assertThat(tableau.carteAvecPosition(position)?.carte).isEqualTo(villageois)
        }

    }

    private fun villageois() = Villageois(
        nom = "Curé",
        cout = 0,
        blasons = listOf(Blason.RELIGIEUX)
    )

    @Nested
    inner class Deplacement {

        private fun tableauAvecUneCartePositionnee(carte: Carte, position: Position): Tableau {
            val tableau = Tableau()
            repository.ajouteCarte(tableau, carte, position)
            return tableau
        }


        @Nested
        inner class Gauche {

            @ParameterizedTest
            @CsvSource(
                value = [
                    "HAUTMILIEU,HAUTGAUCHE",
                    "HAUTDROITE,HAUTMILIEU",
                    "MILIEUMILIEU,MILIEUGAUCHE",
                    "MILIEUDROITE,MILIEUMILIEU",
                    "BASMILIEU,BASGAUCHE",
                    "BASDROITE,BASMILIEU",
                ]
            )
            fun `doit deplacer toutes les cartes d'un tableau vers la gauche lorsqu'il n'y a pas de cartes a gauche`(
                positionInitiale: Position,
                positionFinale: Position
            ) {
                val villageois = villageois()
                val tableau = tableauAvecUneCartePositionnee(villageois, positionInitiale)

                val deplace = repository.deplaceAGauche(tableau)

                assertThat(deplace).isTrue()
                assertThat(tableau.carteAvecPosition(positionFinale)?.carte).isEqualTo(villageois)
            }

            @ParameterizedTest
            @CsvSource(
                value = [
                    "HAUTGAUCHE",
                    "MILIEUGAUCHE",
                    "BASGAUCHE",
                ]
            )
            fun `doit renvoyer faux et ne pas deplacer les cartes d'un tableau vers la gauche lorsqu'il y a des cartes a gauche`(
                positionInitiale: Position,
            ) {
                val villageois = villageois()
                val tableau = tableauAvecUneCartePositionnee(villageois, positionInitiale)

                val deplace = repository.deplaceAGauche(tableau)

                assertThat(deplace).isFalse()
                assertThat(tableau.carteAvecPosition(positionInitiale)?.carte).isEqualTo(villageois)
            }
        }

        @Nested
        inner class Droite {

            @ParameterizedTest
            @CsvSource(
                value = [
                    "HAUTGAUCHE,HAUTMILIEU",
                    "HAUTMILIEU,HAUTDROITE",
                    "MILIEUGAUCHE,MILIEUMILIEU",
                    "MILIEUMILIEU,MILIEUDROITE",
                    "BASGAUCHE,BASMILIEU",
                    "BASMILIEU,BASDROITE",
                ]
            )
            fun `doit deplacer toutes les cartes d'un tableau vers la droite lorsqu'il n'y a pas de cartes a droite`(
                positionInitiale: Position,
                positionFinale: Position
            ) {
                val villageois = villageois()
                val tableau = tableauAvecUneCartePositionnee(villageois, positionInitiale)

                val deplace = repository.deplaceADroite(tableau)

                assertThat(deplace).isTrue()
                assertThat(tableau.carteAvecPosition(positionFinale)?.carte).isEqualTo(villageois)
            }

            @ParameterizedTest
            @CsvSource(
                value = [
                    "HAUTDROITE",
                    "MILIEUDROITE",
                    "BASDROITE",
                ]
            )
            fun `doit renvoyer faux et ne pas deplacer les cartes d'un tableau vers la droite lorsqu'il y a des cartes a droite`(
                positionInitiale: Position,
            ) {
                val villageois = villageois()
                val tableau = tableauAvecUneCartePositionnee(villageois, positionInitiale)

                val deplace = repository.deplaceADroite(tableau)

                assertThat(deplace).isFalse()
                assertThat(tableau.carteAvecPosition(positionInitiale)?.carte).isEqualTo(villageois)
            }
        }

        @Nested
        inner class Haut {
            @ParameterizedTest
            @CsvSource(
                value = [
                    "MILIEUGAUCHE,HAUTGAUCHE",
                    "MILIEUMILIEU,HAUTMILIEU",
                    "MILIEUDROITE,HAUTDROITE",
                    "BASGAUCHE,MILIEUGAUCHE",
                    "BASMILIEU,MILIEUMILIEU",
                    "BASDROITE,MILIEUDROITE",
                ]
            )
            fun `doit deplacer toutes les cartes d'un tableau vers le haut lorsqu'il n'y a pas de cartes en haut`(
                positionInitiale: Position,
                positionFinale: Position
            ) {
                val villageois = villageois()
                val tableau = tableauAvecUneCartePositionnee(villageois, positionInitiale)

                val deplace = repository.deplaceEnHaut(tableau)

                assertThat(deplace).isTrue()
                assertThat(tableau.carteAvecPosition(positionFinale)?.carte).isEqualTo(villageois)
            }

            @ParameterizedTest
            @CsvSource(
                value = [
                    "HAUTGAUCHE",
                    "HAUTMILIEU",
                    "HAUTDROITE",
                ]
            )
            fun `doit renvoyer faux et ne pas deplacer les cartes d'un tableau vers le haut lorsqu'il y a des cartes en haut`(
                positionInitiale: Position,
            ) {
                val villageois = villageois()
                val tableau = tableauAvecUneCartePositionnee(villageois, positionInitiale)

                val deplace = repository.deplaceEnHaut(tableau)

                assertThat(deplace).isFalse()
                assertThat(tableau.carteAvecPosition(positionInitiale)?.carte).isEqualTo(villageois)
            }
        }

        @Nested
        inner class Bas {
            @ParameterizedTest
            @CsvSource(
                value = [
                    "HAUTGAUCHE,MILIEUGAUCHE",
                    "HAUTMILIEU,MILIEUMILIEU",
                    "HAUTDROITE,MILIEUDROITE",
                    "MILIEUGAUCHE,BASGAUCHE",
                    "MILIEUMILIEU,BASMILIEU",
                    "MILIEUDROITE,BASDROITE",
                ]
            )
            fun `doit deplacer toutes les cartes d'un tableau vers le bas lorsqu'il n'y a pas de cartes en bas`(
                positionInitiale: Position,
                positionFinale: Position
            ) {
                val villageois = villageois()
                val tableau = tableauAvecUneCartePositionnee(villageois, positionInitiale)

                val deplace = repository.deplaceEnBas(tableau)

                assertThat(deplace).isTrue()
                assertThat(tableau.carteAvecPosition(positionFinale)?.carte).isEqualTo(villageois)
            }

            @ParameterizedTest
            @CsvSource(
                value = [
                    "BASGAUCHE",
                    "BASMILIEU",
                    "BASDROITE",
                ]
            )
            fun `doit renvoyer faux et ne pas deplacer les cartes d'un tableau vers le bas lorsqu'il y a des cartes en bas`(
                positionInitiale: Position,
            ) {
                val villageois = villageois()
                val tableau = tableauAvecUneCartePositionnee(villageois, positionInitiale)

                val deplace = repository.deplaceEnBas(tableau)

                assertThat(deplace).isFalse()
                assertThat(tableau.carteAvecPosition(positionInitiale)?.carte).isEqualTo(villageois)
            }
        }
    }
}