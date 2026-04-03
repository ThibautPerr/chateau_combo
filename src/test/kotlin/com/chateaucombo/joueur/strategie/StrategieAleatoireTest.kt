package com.chateaucombo.joueur.strategie

import com.chateaucombo.deck.DeckBuilder
import com.chateaucombo.deck.model.CarteVerso
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.Position.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class StrategieAleatoireTest {
    private val strategie = StrategieAleatoire()
    private val deckBuilder = DeckBuilder()

    @Nested
    inner class ChoisitActionCle {
        @RepeatedTest(30)
        fun `doit renvoyer une action valide`() {
            val joueur = Joueur(id = 1)

            val action = strategie.choisitActionCle(joueur, emptyList())

            assertThat(action).isIn(ActionCle.RIEN, ActionCle.RAFRAICHIT, ActionCle.CHANGE_DECK)
        }

        @RepeatedTest(100)
        fun `doit pouvoir renvoyer chacune des trois actions`() {
            val joueur = Joueur(id = 1)
            val actionsObtenues = (1..100).map { strategie.choisitActionCle(joueur, emptyList()) }.toSet()

            assertThat(actionsObtenues).containsExactlyInAnyOrder(ActionCle.RIEN, ActionCle.RAFRAICHIT, ActionCle.CHANGE_DECK)
        }
    }

    @Nested
    inner class ChoisitUnDeplacement {
        @Test
        fun `doit toujours renvoyer AUCUN`() {
            val joueur = Joueur(id = 1)

            val deplacement = strategie.choisitUnDeplacement(joueur)

            assertThat(deplacement).isEqualTo(DirectionDeplacement.AUCUN)
        }
    }

    @Nested
    inner class ChoisitUneCarte {
        @RepeatedTest(20)
        fun `doit choisir une carte parmi les cartes achetables`() {
            val cartesAchetables = listOf(deckBuilder.cure(), deckBuilder.fermiere())
            val cartesDisponibles = listOf(deckBuilder.cure(), deckBuilder.fermiere(), deckBuilder.horlogere())

            val carte = strategie.choisitUneCarte(cartesAchetables, cartesDisponibles)

            assertThat(cartesAchetables).contains(carte)
        }

        @Test
        fun `doit renvoyer une carte verso si aucune carte n'est achetable`() {
            val cartesDisponibles = listOf(deckBuilder.mercenaire(), deckBuilder.milicien(), deckBuilder.horlogere())

            val carte = strategie.choisitUneCarte(emptyList(), cartesDisponibles)

            assertThat(carte).isInstanceOf(CarteVerso::class.java)
            assertThat(cartesDisponibles).contains((carte as CarteVerso).carteOriginale)
        }

        @RepeatedTest(20)
        fun `la carte verso choisie provient des cartes disponibles`() {
            val cartesDisponibles = listOf(deckBuilder.mercenaire(), deckBuilder.milicien(), deckBuilder.horlogere())

            val carte = strategie.choisitUneCarte(emptyList(), cartesDisponibles) as CarteVerso

            assertThat(cartesDisponibles).contains(carte.carteOriginale)
        }
    }

    @Nested
    inner class ChoisitUnePosition {
        @RepeatedTest(20)
        fun `doit renvoyer une position parmi les positions autorisees`() {
            val positionsAutorisees = listOf(HAUTMILIEU, MILIEUGAUCHE, MILIEUDROITE, BASMILIEU)

            val position = strategie.choisitUnePosition(positionsAutorisees)

            assertThat(positionsAutorisees).contains(position)
        }

        @Test
        fun `doit renvoyer la seule position disponible`() {
            val position = strategie.choisitUnePosition(listOf(MILIEUMILIEU))

            assertThat(position).isEqualTo(MILIEUMILIEU)
        }
    }
}
