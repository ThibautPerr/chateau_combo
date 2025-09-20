package com.chateaucombo.player

import com.chateaucombo.carte.model.Blason
import com.chateaucombo.carte.model.Carte
import com.chateaucombo.carte.model.Chatelain
import com.chateaucombo.carte.model.Villageois
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.joueur.repository.JoueurRepository
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.repository.TableauRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class JoueurRepositoryTest {
    private val tableauRepository = TableauRepository()

    private val joueurRepository = JoueurRepository(tableauRepository)

    private fun villageois() = Villageois(
        nom = "Curé",
        cout = 0,
        blasons = listOf(Blason.RELIGIEUX)
    )

    @Nested
    inner class PrendUneCarte {

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
        fun `un joueur prend un chatelain et le place`(position: Position) {
            val joueur = Joueur(id = 1)
            val chatelain = Chatelain(
                nom = "Aumônier",
                cout = 5,
                blasons = listOf(Blason.RELIGIEUX)
            )

            joueurRepository.prendUneCarte(joueur, chatelain, position)

            assertThat(joueur.tableau.cartesPositionees).hasSize(1)
            assertThat(joueur.tableau.cartesPositionees.first().carte).isEqualTo(chatelain)
            assertThat(joueur.tableau.cartesPositionees.first().position).isEqualTo(position)
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
        fun `un joueur prend un villageois et le place`(position: Position) {
            val joueur = Joueur(id = 1)
            val villageois = villageois()

            joueurRepository.prendUneCarte(joueur, villageois, position)

            assertThat(joueur.tableau.cartesPositionees).hasSize(1)
            assertThat(joueur.tableau.cartesPositionees.first().carte).isEqualTo(villageois)
            assertThat(joueur.tableau.cartesPositionees.first().position).isEqualTo(position)
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
        fun `renvoie faux et empeche l'ajout d'une carte sur une position deja occupee`(position: Position) {
            val joueur = Joueur(id = 1)
            val villageois = villageois()

            val premierCoup = joueurRepository.prendUneCarte(joueur, villageois, position)
            val deuxiemeCoup = joueurRepository.prendUneCarte(joueur, villageois, position)

            assertThat(premierCoup).isTrue()
            assertThat(deuxiemeCoup).isFalse()
            assertThat(joueur.tableau.cartesPositionees.size).isEqualTo(1)
            assertThat(joueur.tableau.carteAvecPosition(position)?.carte).isEqualTo(villageois)
        }
    }

    @Nested
    inner class Deplacement {

        private fun joueurAvecUneCarte(carte: Carte, position: Position): Joueur {
            val joueur = Joueur(id = 1)
            joueurRepository.prendUneCarte(joueur, carte, position)
            return joueur
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
            fun `deplace toutes les cartes d'un joueur vers la gauche lorsqu'il n'y a pas de cartes a gauche`(
                positionInitiale: Position,
                positionFinale: Position
            ) {
                val villageois = villageois()
                val joueur = joueurAvecUneCarte(villageois, positionInitiale)

                val deplace = joueurRepository.deplaceAGauche(joueur)

                assertThat(deplace).isTrue()
                assertThat(joueur.tableau.carteAvecPosition(positionFinale)?.carte).isEqualTo(villageois)
            }

            @ParameterizedTest
            @CsvSource(
                value = [
                    "HAUTGAUCHE",
                    "MILIEUGAUCHE",
                    "BASGAUCHE",
                ]
            )
            fun `renvoie faux et ne deplace pas les cartes d'un joueur vers la gauche lorsqu'il y a des cartes a gauche`(
                positionInitiale: Position,
            ) {
                val villageois = villageois()
                val joueur = joueurAvecUneCarte(villageois, positionInitiale)

                val deplace = joueurRepository.deplaceAGauche(joueur)

                assertThat(deplace).isFalse()
                assertThat(joueur.tableau.carteAvecPosition(positionInitiale)?.carte).isEqualTo(villageois)
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
            fun `deplace toutes les cartes d'un joueur vers la droite lorsqu'il n'y a pas de cartes a droite`(
                positionInitiale: Position,
                positionFinale: Position
            ) {
                val villageois = villageois()
                val joueur = joueurAvecUneCarte(villageois, positionInitiale)

                val deplace = joueurRepository.deplaceADroite(joueur)

                assertThat(deplace).isTrue()
                assertThat(joueur.tableau.carteAvecPosition(positionFinale)?.carte).isEqualTo(villageois)
            }

            @ParameterizedTest
            @CsvSource(
                value = [
                    "HAUTDROITE",
                    "MILIEUDROITE",
                    "BASDROITE",
                ]
            )
            fun `renvoie faux et ne deplace pas les cartes d'un joueur vers la droite lorsqu'il y a des cartes a droite`(
                positionInitiale: Position,
            ) {
                val villageois = villageois()
                val joueur = joueurAvecUneCarte(villageois, positionInitiale)

                val deplace = joueurRepository.deplaceADroite(joueur)

                assertThat(deplace).isFalse()
                assertThat(joueur.tableau.carteAvecPosition(positionInitiale)?.carte).isEqualTo(villageois)
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
            fun `deplace toutes les cartes d'un joueur vers le haut lorsqu'il n'y a pas de cartes en haut`(
                positionInitiale: Position,
                positionFinale: Position
            ) {
                val villageois = villageois()
                val joueur = joueurAvecUneCarte(villageois, positionInitiale)

                val deplace = joueurRepository.deplaceEnHaut(joueur)

                assertThat(deplace).isTrue()
                assertThat(joueur.tableau.carteAvecPosition(positionFinale)?.carte).isEqualTo(villageois)
            }

            @ParameterizedTest
            @CsvSource(
                value = [
                    "HAUTGAUCHE",
                    "HAUTMILIEU",
                    "HAUTDROITE",
                ]
            )
            fun `renvoie faux et ne deplace pas les cartes d'un joueur vers le haut lorsqu'il y a des cartes en haut`(
                positionInitiale: Position,
            ) {
                val villageois = villageois()
                val joueur = joueurAvecUneCarte(villageois, positionInitiale)

                val deplace = joueurRepository.deplaceEnHaut(joueur)

                assertThat(deplace).isFalse()
                assertThat(joueur.tableau.carteAvecPosition(positionInitiale)?.carte).isEqualTo(villageois)
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
            fun `deplace toutes les cartes d'un joueur vers le bas lorsqu'il n'y a pas de cartes en bas`(
                positionInitiale: Position,
                positionFinale: Position
            ) {
                val villageois = villageois()
                val joueur = joueurAvecUneCarte(villageois, positionInitiale)

                val deplace = joueurRepository.deplaceEnBas(joueur)

                assertThat(deplace).isTrue()
                assertThat(joueur.tableau.carteAvecPosition(positionFinale)?.carte).isEqualTo(villageois)
            }

            @ParameterizedTest
            @CsvSource(
                value = [
                    "BASGAUCHE",
                    "BASMILIEU",
                    "BASDROITE",
                ]
            )
            fun `renvoie faux et ne deplace pas les cartes d'un joueur vers le bas lorsqu'il y a des cartes en bas`(
                positionInitiale: Position,
            ) {
                val villageois = villageois()
                val joueur = joueurAvecUneCarte(villageois, positionInitiale)

                val deplace = joueurRepository.deplaceEnBas(joueur)

                assertThat(deplace).isFalse()
                assertThat(joueur.tableau.carteAvecPosition(positionInitiale)?.carte).isEqualTo(villageois)
            }
        }
    }
}