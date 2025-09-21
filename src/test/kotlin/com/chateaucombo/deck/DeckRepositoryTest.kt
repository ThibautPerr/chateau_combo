package com.chateaucombo.deck

import com.chateaucombo.deck.model.Blason.*
import com.chateaucombo.deck.model.Chatelain
import com.chateaucombo.deck.model.Deck
import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.deck.repository.DeckRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

class DeckRepositoryTest {
    private val repository = DeckRepository()

    @Nested
    inner class CreeDeuxDecks {
        @Test
        fun `doit renvoyer un deck de chatelains et un deck de villageois`() {
            val file = givenUnFichierAvecPlusieursCartes()

            val (chatelains, villageois) = repository.creeDeuxDecksChatelainsEtVillageoisDepuis(file)

            assertThat(chatelains.cartes).hasSize(39)
            assertThat(chatelains.cartesDisponibles).hasSize(0)
            assertThat(villageois.cartes).hasSize(39)
            assertThat(villageois.cartesDisponibles).hasSize(0)
        }

        private fun givenUnFichierAvecPlusieursCartes() = File("src/test/resources/cartes.json")

        @Test
        fun `doit renvoyer un deck chatelains contenant une carte Alchimiste avec ses attributs`() {
            val file = givenUnFichierAvecUnAlchimiste()

            val (chatelains, _) = repository.creeDeuxDecksChatelainsEtVillageoisDepuis(file)

            assertThat(chatelains.cartes).hasSize(1)
            assertThat(chatelains.cartes.first().nom).isEqualTo("Alchimiste")
            assertThat(chatelains.cartes.first()).isInstanceOf(Chatelain::class.java)
            assertThat(chatelains.cartes.first().cout).isEqualTo(6)
            assertThat(chatelains.cartes.first().blasons).isEqualTo(listOf(ERUDIT))
        }

        private fun givenUnFichierAvecUnAlchimiste(): File {
            val file = File("target/carte.json")
            file.writeText(alchimisteJson())
            return file
        }

        private fun alchimisteJson() =
            """
            [
              {
                "type": "CHATELAIN",
                "nom": "Alchimiste",
                "cout": 6,
                "blasons": ["ERUDIT"]
              }
            ]
        """.trimIndent()

        @Test
        fun `doit renvoyer un deck villageois contenant une carte Espion avec ses attributs`() {
            val file = givenUnFichierAvecUnEspion()

            val (_, villageois) = repository.creeDeuxDecksChatelainsEtVillageoisDepuis(file)

            assertThat(villageois.cartes).hasSize(1)
            assertThat(villageois.cartes.first().nom).isEqualTo("Espion")
            assertThat(villageois.cartes.first()).isInstanceOf(Villageois::class.java)
            assertThat(villageois.cartes.first().cout).isEqualTo(4)
            assertThat(villageois.cartes.first().blasons).isEqualTo(listOf(MILITAIRE, ERUDIT))
        }

        private fun givenUnFichierAvecUnEspion(): File {
            val file = File("target/carte.json")
            file.writeText(espionJson())
            return file
        }

        private fun espionJson() =
            """
            [
              {
                "nom": "Espion",
                "type": "VILLAGEOIS",
                "cout": 4,
                "blasons": [
                  "MILITAIRE",
                  "ERUDIT"
                ]
              }
            ]
        """.trimIndent()
    }

    @Nested
    inner class Melange {
        @Test
        fun `doit melanger un deck`() {
            val deck = deckAvecDesCartes()

            val deckMelange = repository.melange(deck)

            assertThat(deckMelange.toutesLesCartesDansLOrdreDeDepart()).isFalse()
        }

        private fun deckAvecDesCartes() =
            Deck(
                cartes = listOf(
                    Villageois(
                        nom = "Curé",
                        cout = 0,
                        blasons = listOf(RELIGIEUX)
                    ),
                    Villageois(
                        nom = "Écuyer",
                        cout = 0,
                        blasons = listOf(MILITAIRE)
                    ),
                    Villageois(
                        nom = "Épicière",
                        cout = 0,
                        blasons = listOf(ARTISAN)
                    ),
                    Villageois(
                        nom = "Fermière",
                        cout = 0,
                        blasons = listOf(PAYSAN)
                    ),
                    Villageois(
                        nom = "Horlogère",
                        cout = 3,
                        blasons = listOf(ARTISAN)
                    ),
                    Villageois(
                        nom = "Mendiante",
                        cout = 0,
                        blasons = listOf(PAYSAN)
                    ),
                    Villageois(
                        nom = "Milicien",
                        cout = 2,
                        blasons = listOf(MILITAIRE)
                    )
                )
            )

        private fun Deck.toutesLesCartesDansLOrdreDeDepart(): Boolean =
            this.cartes.first().nom == "Curé"
                    && this.cartes[1].nom == "Écuyer"
                    && this.cartes[2].nom == "Épicière"
                    && this.cartes[3].nom == "Fermière"
                    && this.cartes[4].nom == "Horlogère"
                    && this.cartes[5].nom == "Mendiante"
                    && this.cartes[6].nom == "Milicien"
    }

    @Nested
    inner class RemplitCartesDispo {
        @Test
        fun `doit remplir zero cartes disponibles avec trois cartes`() {
            val deck = deckAvecQuatreCartesEtAucuneCarteDisponible()

            val nouveauDeck = repository.remplitLesCartesDisponibles(deck)

            assertThat(nouveauDeck.cartes).hasSize(1)
            assertThat(nouveauDeck.cartes.first()).isEqualTo(fermiere())
            assertThat(nouveauDeck.cartesDisponibles).hasSize(3)
            assertThat(nouveauDeck.cartesDisponibles.first()).isEqualTo(cure())
            assertThat(nouveauDeck.cartesDisponibles[1]).isEqualTo(ecuyer())
            assertThat(nouveauDeck.cartesDisponibles[2]).isEqualTo(epiciere())
        }

        private fun deckAvecQuatreCartesEtAucuneCarteDisponible() =
            Deck(cartes = listOf(cure(), ecuyer(), epiciere(), fermiere()))

        private fun cure() = Villageois(
            nom = "Curé",
            cout = 0,
            blasons = listOf(RELIGIEUX)
        )

        private fun ecuyer() = Villageois(
            nom = "Écuyer",
            cout = 0,
            blasons = listOf(MILITAIRE)
        )

        private fun epiciere() = Villageois(
            nom = "Épicière",
            cout = 0,
            blasons = listOf(ARTISAN)
        )

        private fun fermiere() = Villageois(
            nom = "Fermière",
            cout = 0,
            blasons = listOf(PAYSAN)
        )

        @Test
        fun `doit remplir une liste de cartes disponibles non vide pour avoir trois cartes`() {
            val deck = deckAvecTroisCartesEtUneCarteDisponible()

            val nouveauDeck = repository.remplitLesCartesDisponibles(deck)

            assertThat(nouveauDeck.cartes).hasSize(1)
            assertThat(nouveauDeck.cartes.first()).isEqualTo(fermiere())
            assertThat(nouveauDeck.cartesDisponibles).hasSize(3)
            assertThat(nouveauDeck.cartesDisponibles.first()).isEqualTo(cure())
            assertThat(nouveauDeck.cartesDisponibles[1]).isEqualTo(ecuyer())
            assertThat(nouveauDeck.cartesDisponibles[2]).isEqualTo(epiciere())
        }

        private fun deckAvecTroisCartesEtUneCarteDisponible() =
            Deck(
                cartes = listOf(ecuyer(), epiciere(), fermiere()),
                cartesDisponibles = listOf(cure())
            )

    }

}