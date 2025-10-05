package com.chateaucombo

import com.chateaucombo.deck.repository.DeckRepository
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.joueur.repository.JoueurRepository
import com.chateaucombo.tableau.repository.TableauRepository
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.io.File

class ChateauComboTest {
    private val joueurRepository = spyk(JoueurRepository(TableauRepository(), DeckRepository()))

    private val deckRepository = DeckRepository()

    private val app = ChateauCombo(joueurRepository, deckRepository)

    private fun play(joueurs: List<Joueur>) {
        val fichier = fichierAvecToutesLesCartes()
        val (chatelains, villageois) = deckRepository.creeDeuxDecksChatelainsEtVillageoisDepuis(fichier)
        app.play(joueurs = joueurs, deckChatelains = chatelains, deckVillageois = villageois)
    }

    private fun fichierAvecToutesLesCartes() = File("src/test/resources/cartes.json")

    @Test
    fun `should play a game with 4 players`() {
        val joueurs = quatreJoueurs()

        play(joueurs)

        joueurs.forEach { joueur ->
            verify(exactly = 9) {
                joueurRepository.choisitUneCarte(joueur, any())
                joueurRepository.choisitUnePosition(joueur)
                joueurRepository.placeUneCarte(joueur, any(), any())
            }
        }
    }

    private fun quatreJoueurs() = List(4) { Joueur(id = it) }
}
