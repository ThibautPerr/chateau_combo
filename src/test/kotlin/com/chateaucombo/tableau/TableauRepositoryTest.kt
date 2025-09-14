package com.chateaucombo.tableau

import com.chateaucombo.carte.model.Blason
import com.chateaucombo.carte.model.Villageois
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.model.Tableau
import com.chateaucombo.tableau.repository.TableauRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TableauRepositoryTest {
    private val repository = TableauRepository()

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
        val tableau = Tableau()
        val villageois = villageois()

        val premierCoup = repository.ajouteCarte(tableau, villageois, position)
        val deuxiemeCoup = repository.ajouteCarte(tableau, villageois, position)

        assertThat(premierCoup).isTrue()
        assertThat(deuxiemeCoup).isFalse()
        assertThat(tableau.cartesPositionees.size).isEqualTo(1)
        assertThat(tableau.carteAvecPosition(position)?.carte).isEqualTo(villageois)
    }

    private fun villageois() = Villageois(
        nom = "Curé",
        cout = 0,
        blasons = listOf(Blason.RELIGIEUX)
    )
}