package com.chateaucombo.deck

import com.chateaucombo.deck.model.Blason.ERUDIT
import com.chateaucombo.deck.model.Blason.MILITAIRE
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

    private val deckBuilder = DeckBuilder()

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
            val deck = deckBuilder.deckAvecDesCartes()

            val deckMelange = repository.melange(deck)

            assertThat(deckMelange.toutesLesCartesDansLOrdreDeDepart()).isFalse()
        }

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
            val deck = deckBuilder.deckAvecQuatreCartesEtAucuneCarteDisponible()

            val nouveauDeck = repository.remplitLesCartesDisponibles(deck)

            assertThat(nouveauDeck.cartes).hasSize(1)
            assertThat(nouveauDeck.cartes.first()).isEqualTo(deckBuilder.fermiere())
            assertThat(nouveauDeck.cartesDisponibles).hasSize(3)
            assertThat(nouveauDeck.cartesDisponibles.first()).isEqualTo(deckBuilder.cure())
            assertThat(nouveauDeck.cartesDisponibles[1]).isEqualTo(deckBuilder.ecuyer())
            assertThat(nouveauDeck.cartesDisponibles[2]).isEqualTo(deckBuilder.epiciere())
        }

        @Test
        fun `doit remplir une liste de cartes disponibles non vide pour avoir trois cartes`() {
            val deck = deckBuilder.deckAvecTroisCartesEtUneCarteDisponible()

            val nouveauDeck = repository.remplitLesCartesDisponibles(deck)

            assertThat(nouveauDeck.cartes).hasSize(1)
            assertThat(nouveauDeck.cartes.first()).isEqualTo(deckBuilder.fermiere())
            assertThat(nouveauDeck.cartesDisponibles).hasSize(3)
            assertThat(nouveauDeck.cartesDisponibles.first()).isEqualTo(deckBuilder.cure())
            assertThat(nouveauDeck.cartesDisponibles[1]).isEqualTo(deckBuilder.ecuyer())
            assertThat(nouveauDeck.cartesDisponibles[2]).isEqualTo(deckBuilder.epiciere())
        }

    }

}