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

            repository.melange(deck)

            assertThat(deck.toutesLesCartesDansLOrdreDeDepart()).isFalse()
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

            repository.remplitLesCartesDisponibles(deck)

            assertThat(deck.cartes).hasSize(1)
            assertThat(deck.cartes.first()).isEqualTo(deckBuilder.fermiere())
            assertThat(deck.cartesDisponibles).hasSize(3)
            assertThat(deck.cartesDisponibles.first()).isEqualTo(deckBuilder.cure())
            assertThat(deck.cartesDisponibles[1]).isEqualTo(deckBuilder.ecuyer())
            assertThat(deck.cartesDisponibles[2]).isEqualTo(deckBuilder.epiciere())
        }

        @Test
        fun `doit remplir une liste de cartes disponibles non vide pour avoir trois cartes`() {
            val deck = deckBuilder.deckAvecTroisCartesEtUneCarteDisponible()

            repository.remplitLesCartesDisponibles(deck)

            assertThat(deck.cartes).hasSize(1)
            assertThat(deck.cartes.first()).isEqualTo(deckBuilder.fermiere())
            assertThat(deck.cartesDisponibles).hasSize(3)
            assertThat(deck.cartesDisponibles.first()).isEqualTo(deckBuilder.cure())
            assertThat(deck.cartesDisponibles[1]).isEqualTo(deckBuilder.ecuyer())
            assertThat(deck.cartesDisponibles[2]).isEqualTo(deckBuilder.epiciere())
        }

    }

    @Nested
    inner class RafraichirLeDeck {
        @Test
        fun `doit defausser les cartes disponibles et remplir les cartes disponibles`() {
            val cartesDisponibles = listOf(deckBuilder.cure(), deckBuilder.fermiere(), deckBuilder.horlogere())
            val nouvellesCartesDisponibles = listOf(deckBuilder.fermiere(), deckBuilder.milicien(), deckBuilder.mendiante())
            val deck = deckBuilder.deckAvecTroisCartesDispos(cartesDisponibles, nouvellesCartesDisponibles)

            repository.rafraichitLeDeck(deck)

            assertThat(deck.cartesDisponibles).isEqualTo(nouvellesCartesDisponibles)
            assertThat(deck.defausse).containsAll(cartesDisponibles)
        }

        @Test
        fun `doit remettre les cartes de la defausse dans le deck s'il n'y a plus de cartes dans le deck`() {
            val cartesDisponibles = listOf(deckBuilder.cure(), deckBuilder.fermiere(), deckBuilder.horlogere())
            val nouvellesCartesDisponibles = listOf(deckBuilder.fermiere())
            val deck = deckBuilder.deckAvecTroisCartesDispos(cartesDisponibles, nouvellesCartesDisponibles)

            repository.rafraichitLeDeck(deck)

            assertThat(deck.cartesDisponibles).hasSize(3)
            assertThat(deck.cartesDisponibles.first()).isEqualTo(deckBuilder.fermiere())
            assertThat(cartesDisponibles).contains(deck.cartesDisponibles[1])
            assertThat(cartesDisponibles).contains(deck.cartesDisponibles[2])
            assertThat(deck.defausse).isEmpty()
        }
    }

}