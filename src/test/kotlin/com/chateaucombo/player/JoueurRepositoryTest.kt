package com.chateaucombo.player

import com.chateaucombo.card.model.Blason
import com.chateaucombo.card.model.Chatelain
import com.chateaucombo.card.model.Villageois
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.joueur.repository.JoueurRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JoueurRepositoryTest {
    private val repository = JoueurRepository()
    @Test
    fun `un joueur prend un chatelain`() {
        val joueur = Joueur(id = 1)
        val chatelain = Chatelain(
            cout = 5,
            blasons = listOf(Blason.RELIGIEUX)
        )

        repository.prendUneCarte(joueur, chatelain)

        assertThat(joueur.cartes).hasSize(1)
        assertThat(joueur.cartes.first()).isEqualTo(chatelain)
    }

    @Test
    fun `un joueur prend un villageois`() {
        val joueur = Joueur(id = 1)
        val villageois = Villageois(
            cout = 5,
            blasons = listOf(Blason.RELIGIEUX)
        )

        repository.prendUneCarte(joueur, villageois)

        assertThat(joueur.cartes).hasSize(1)
        assertThat(joueur.cartes.first()).isEqualTo(villageois)
    }
}