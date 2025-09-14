package com.chateaucombo

import com.chateaucombo.joueur.model.Joueur
import org.junit.jupiter.api.Test

class ChateauComboTest {
    private val app = ChateauCombo()

    @Test
    fun `should play one game`() {
        play()
    }

    private fun play(joueurs: List<Joueur> = given4Joueurs()) {
        app.play(joueurs)
    }

    @Test
    fun `should play a game with 4 players`() {
        val joueurs = given4Joueurs()

        play(joueurs)
    }

    private fun given4Joueurs() = List(4) { Joueur(id = it) }
}