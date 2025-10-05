package com.chateaucombo.joueur

import com.chateaucombo.deck.DeckBuilder
import com.chateaucombo.deck.model.*
import com.chateaucombo.deck.repository.DeckRepository
import com.chateaucombo.effet.model.Effets
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.joueur.repository.JoueurRepository
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.model.Position.*
import com.chateaucombo.tableau.model.Tableau
import com.chateaucombo.tableau.repository.TableauRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class JoueurRepositoryTest {
    private val tableauRepository = TableauRepository()

    private val deckRepository = DeckRepository()

    private val joueurRepository = JoueurRepository(tableauRepository, deckRepository)

    private val deckBuilder = DeckBuilder()

    private fun villageois() = Villageois(
        nom = "Curé",
        cout = 0,
        blasons = listOf(Blason.RELIGIEUX),
        effets = Effets()
    )

    @Nested
    inner class ChoisitUneCarte {
        @RepeatedTest(10)
        fun `doit choisir une carte parmi les cartes du deck disponible`() {
            val joueur = Joueur(id = 1, or = 15)
            val cartesDisponibles = listOf(deckBuilder.cure(), deckBuilder.ecuyer(), deckBuilder.epiciere())
            val deck = deckBuilder.deckAvecTroisCartesDispos(cartesDisponibles)

            val carteChoisie = joueurRepository.choisitUneCarte(joueur, deck)

            assertThat(cartesDisponibles).contains(carteChoisie)
        }

        @RepeatedTest(10)
        fun `doit choisir une carte qu'il peut acheter`() {
            val joueur = Joueur(id = 1, or = 0)
            val cartesDisponibles = listOf(deckBuilder.cure(), deckBuilder.fermiere(), deckBuilder.horlogere())
            val deck = deckBuilder.deckAvecTroisCartesDispos(cartesDisponibles)
            val cartesAchetables = listOf(deckBuilder.cure(), deckBuilder.fermiere())

            val carteChoisie = joueurRepository.choisitUneCarte(joueur, deck)

            assertThat(cartesAchetables).contains(carteChoisie)
        }

        @Test
        fun `doit choisir une carte face verso s'il n'a pas assez d'or pour acheter une carte disponible`() {
            val joueur = Joueur(id = 1, or = 0)
            val cartesDisponibles = listOf(deckBuilder.mercenaire(), deckBuilder.milicien(), deckBuilder.horlogere())
            val deck = deckBuilder.deckAvecTroisCartesDispos(cartesDisponibles)
            val cartesVersos: List<Carte> = cartesDisponibles.map { it.toCarteVerso() }

            val carteChoisie = joueurRepository.choisitUneCarte(joueur, deck)

            assertThat(cartesVersos).contains(carteChoisie)
        }

        private fun Carte.toCarteVerso() = CarteVerso(nom = "Carte Verso (${this.nom})", carteOriginale = this)

        @Test
        fun `doit payer pour choisir une carte`() {
            val orInitial = 10
            val joueur = Joueur(id = 1, or = orInitial)
            val cartesDisponibles = listOf(deckBuilder.mercenaire(), deckBuilder.milicien(), deckBuilder.horlogere())
            val deck = deckBuilder.deckAvecTroisCartesDispos(cartesDisponibles)

            val carteChoisie = joueurRepository.choisitUneCarte(joueur, deck)

            assertThat(joueur.or).isEqualTo(orInitial - carteChoisie.cout)
        }

        @Test
        fun `doit gagner 6 or et deux cles lorsqu'il choisit une carte verso`() {
            val orInitial = 0
            val cleInitial = 2
            val joueur = Joueur(id = 1, or = orInitial, cle = cleInitial)
            val cartesDisponibles = listOf(deckBuilder.mercenaire(), deckBuilder.milicien(), deckBuilder.horlogere())
            val deck = deckBuilder.deckAvecTroisCartesDispos(cartesDisponibles)

            val carteChoisie = joueurRepository.choisitUneCarte(joueur, deck)

            assertThat(carteChoisie).isInstanceOf(CarteVerso::class.java)
            assertThat(joueur.or).isEqualTo(orInitial + 6)
            assertThat(joueur.cle).isEqualTo(cleInitial + 2)
        }

        @Test
        fun `doit retirer la carte choisie des cartes disponibles du deck`() {
            val joueur = Joueur(id = 1, or = 15)
            val cartesDisponibles = listOf(deckBuilder.mercenaire(), deckBuilder.milicien(), deckBuilder.horlogere())
            val deck = deckBuilder.deckAvecTroisCartesDispos(cartesDisponibles)

            val carteChoisie = joueurRepository.choisitUneCarte(joueur, deck)

            assertThat(deck.cartesDisponibles).hasSize(2)
            assertThat(deck.cartesDisponibles).isEqualTo(cartesDisponibles.minus(carteChoisie))
        }
    }

    @Nested
    inner class ChoisitUnePosition {
        @Test
        fun `doit renvoyer la position milieu milieu s'il n'y a aucune carte`() {
            val joueur = Joueur(id = 1)

            val positionChoisie = joueurRepository.choisitUnePosition(joueur)

            assertThat(positionChoisie).isEqualTo(MILIEUMILIEU)
        }

        @RepeatedTest(20)
        fun `doit renvoyer la position d'une case adjacente a la carte posee au milieu`() {
            val tableau = tableauAvecUneCarteAuMilieuMilieu()
            val joueur = Joueur(id = 1, tableau = tableau)
            val positionAutorisees = listOf(HAUTMILIEU, MILIEUGAUCHE, MILIEUDROITE, BASMILIEU)

            val positionChoisie = joueurRepository.choisitUnePosition(joueur)

            assertThat(positionAutorisees).contains(positionChoisie)
        }

        private fun tableauAvecUneCarteAuMilieuMilieu() =
            Tableau(cartesPositionees = mutableListOf(CartePositionee(deckBuilder.cure(), MILIEUMILIEU)))

        @RepeatedTest(20)
        fun `doit renvoyer la position d'une case adjacente dans un tableau avec trois cartes au milieu vertical`() {
            val tableau = tableauAvecTroisCartesAuMilieuVertical()
            val joueur = Joueur(id = 1, tableau = tableau)
            val positionAutorisees = listOf(HAUTGAUCHE, HAUTDROITE, MILIEUGAUCHE, MILIEUDROITE, BASGAUCHE, BASDROITE)

            val positionChoisie = joueurRepository.choisitUnePosition(joueur)

            assertThat(positionAutorisees).contains(positionChoisie)
        }

        private fun tableauAvecTroisCartesAuMilieuVertical() =
            Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(deckBuilder.cure(), HAUTMILIEU),
                    CartePositionee(deckBuilder.cure(), MILIEUMILIEU),
                    CartePositionee(deckBuilder.cure(), BASMILIEU),
                )
            )

        @RepeatedTest(20)
        fun `doit renvoyer la position d'une case adjacente dans un tableau avec trois cartes au milieu horizontal`() {
            val tableau = tableauAvecTroisCartesAuMilieuHorizontal()
            val joueur = Joueur(id = 1, tableau = tableau)
            val positionAutorisees = listOf(HAUTGAUCHE, HAUTMILIEU, HAUTDROITE, BASGAUCHE, BASMILIEU, BASDROITE)

            val positionChoisie = joueurRepository.choisitUnePosition(joueur)

            assertThat(positionAutorisees).contains(positionChoisie)
        }

        private fun tableauAvecTroisCartesAuMilieuHorizontal() =
            Tableau(
                cartesPositionees = mutableListOf(
                    CartePositionee(deckBuilder.cure(), MILIEUGAUCHE),
                    CartePositionee(deckBuilder.cure(), MILIEUMILIEU),
                    CartePositionee(deckBuilder.cure(), MILIEUDROITE),
                )
            )
    }

    @Nested
    inner class PlaceUneCarte {

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
        fun `doit placer un chatelain dans le tableau d'un joueur`(position: Position) {
            val joueur = Joueur(id = 1)
            val chatelain = Chatelain(
                nom = "Aumônier",
                cout = 5,
                blasons = listOf(Blason.RELIGIEUX),
                effets = Effets()
            )

            joueurRepository.placeUneCarte(joueur, chatelain, position)

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
        fun `doit placer un villageois dans le tableau d'un joueur`(position: Position) {
            val joueur = Joueur(id = 1)
            val villageois = villageois()

            joueurRepository.placeUneCarte(joueur, villageois, position)

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
        fun `doit renvoyer faux et empecher l'ajout d'une carte sur une position deja occupee`(position: Position) {
            val joueur = Joueur(id = 1)
            val villageois = villageois()

            val premierCoup = joueurRepository.placeUneCarte(joueur, villageois, position)
            val deuxiemeCoup = joueurRepository.placeUneCarte(joueur, villageois, position)

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
            joueurRepository.placeUneCarte(joueur, carte, position)
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
            fun `doit deplacer toutes les cartes d'un joueur vers la gauche lorsqu'il n'y a pas de cartes a gauche`(
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
            fun `doit renvoyer faux et ne pas deplacer les cartes d'un joueur vers la gauche lorsqu'il y a des cartes a gauche`(
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
            fun `doit deplacer toutes les cartes d'un joueur vers la droite lorsqu'il n'y a pas de cartes a droite`(
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
            fun `doit renvoyer faux et ne pas deplacer les cartes d'un joueur vers la droite lorsqu'il y a des cartes a droite`(
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
            fun `doit deplacer toutes les cartes d'un joueur vers le haut lorsqu'il n'y a pas de cartes en haut`(
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
            fun `doit renvoyer faux et ne pas deplacer les cartes d'un joueur vers le haut lorsqu'il y a des cartes en haut`(
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
            fun `doit deplacer toutes les cartes d'un joueur vers le bas lorsqu'il n'y a pas de cartes en bas`(
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
            fun `doit renvoyer faux et ne pas deplacer les cartes d'un joueur vers le bas lorsqu'il y a des cartes en bas`(
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

    @Nested
    inner class RafraichitLeDeck {
        @Test
        fun `doit rafraichir le deck en echange d'une cle`() {
            val cleInitiale = 2
            val joueur = Joueur(id = 1, cle = cleInitiale)
            val cartesDisponibles = listOf(deckBuilder.cure(), deckBuilder.fermiere(), deckBuilder.horlogere())
            val nouvellesCartesDisponibles = listOf(deckBuilder.fermiere(), deckBuilder.milicien(), deckBuilder.mendiante())
            val deck = deckBuilder.deckAvecTroisCartesDispos(cartesDisponibles, nouvellesCartesDisponibles)

            joueurRepository.rafraichitLeDeck(joueur, deck)

            assertThat(joueur.cle).isEqualTo(cleInitiale - 1)
            assertThat(deck.cartesDisponibles).isEqualTo(nouvellesCartesDisponibles)
        }
    }

    @Nested
    inner class ChangeLeDeckActuel {
        @Test
        fun `doit changer le deck actuel en echange d'une cle`() {
            val cleInitiale = 2
            val joueur = Joueur(id = 1, cle = cleInitiale)
            val deckActuel = deckBuilder.deckAvecDesCartes(estLeDeckActuel = true)
            val prochainDeckActuel = deckBuilder.deckAvecDesCartes(estLeDeckActuel = false)

            joueurRepository.changeLeDeckActuel(joueur, deckActuel, prochainDeckActuel)

            assertThat(joueur.cle).isEqualTo(cleInitiale - 1)
            assertThat(deckActuel.estLeDeckActuel).isFalse()
            assertThat(prochainDeckActuel.estLeDeckActuel).isTrue()
        }
    }
}
