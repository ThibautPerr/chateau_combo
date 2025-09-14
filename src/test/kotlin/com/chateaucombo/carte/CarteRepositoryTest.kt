package com.chateaucombo.carte

import com.chateaucombo.carte.model.Blason.ERUDIT
import com.chateaucombo.carte.model.Blason.MILITAIRE
import com.chateaucombo.carte.model.Villageois
import com.chateaucombo.carte.repository.CarteRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class CarteRepositoryTest {
    private val repository = CarteRepository()

    @Test
    fun `le repository doit renvoyer la liste de toutes les cartes`() {
        val file = givenUnFichierAvecPlusieursCartes()

        val cartes = repository.litLesCartesDepuis(file)

        assertThat(cartes).hasSize(78)
    }

    private fun givenUnFichierAvecPlusieursCartes() = File("src/test/resources/cartes.json")

    @Test
    fun `le repository doit renvoyer une carte Espion avec ses attributs`() {
        val file = givenUnFichierAvecUnEspion()

        val cartes = repository.litLesCartesDepuis(file)

        assertThat(cartes).hasSize(1)
        assertThat(cartes.first().nom).isEqualTo("Espion")
        assertThat(cartes.first()).isInstanceOf(Villageois::class.java)
        assertThat(cartes.first().cout).isEqualTo(4)
        assertThat(cartes.first().blasons).isEqualTo(listOf(MILITAIRE, ERUDIT))
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