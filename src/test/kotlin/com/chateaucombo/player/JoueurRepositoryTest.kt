package com.chateaucombo.player

import com.chateaucombo.carte.model.Blason
import com.chateaucombo.carte.model.Chatelain
import com.chateaucombo.carte.model.Villageois
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.joueur.repository.JoueurRepository
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.repository.TableauRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class JoueurRepositoryTest {
    private val tableauRepository = TableauRepository()

    private val joueurRepository = JoueurRepository(tableauRepository)

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

    private fun villageois() = Villageois(
        nom = "Curé",
        cout = 0,
        blasons = listOf(Blason.RELIGIEUX)
    )

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
    fun `un joueur ne peut pas ajouter une carte sur une position deja occupee`(position: Position) {
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