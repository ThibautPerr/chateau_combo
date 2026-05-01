package com.chateaucombo.joueur.strategie.genetique.evolution

import com.chateaucombo.strategie.genetique.ExtracteurFeatures
import com.chateaucombo.strategie.genetique.evolution.Population
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.Random

class PopulationTest {

    @Test
    fun `aleatoire renvoie le nombre demande de genomes a la bonne taille`() {
        val pop = Population.aleatoire(taille = 5, sigmaInit = 1f, random = Random(42))

        assertThat(pop).hasSize(5)
        assertThat(pop).allMatch { it.poids.size == ExtracteurFeatures.NB_FEATURES }
    }

    @Test
    fun `aleatoire est reproductible avec la meme graine`() {
        val a = Population.aleatoire(taille = 4, sigmaInit = 1f, random = Random(123))
        val b = Population.aleatoire(taille = 4, sigmaInit = 1f, random = Random(123))

        assertThat(a).isEqualTo(b)
    }

    @Test
    fun `aleatoire produit des genomes differents avec des graines distinctes`() {
        val a = Population.aleatoire(taille = 4, sigmaInit = 1f, random = Random(1))
        val b = Population.aleatoire(taille = 4, sigmaInit = 1f, random = Random(2))

        assertThat(a).isNotEqualTo(b)
    }

    @Test
    fun `aleatoire avec sigma 0 produit des genomes nuls`() {
        val pop = Population.aleatoire(taille = 3, sigmaInit = 0f, random = Random(0))

        assertThat(pop).allMatch { it.poids.all { p -> p == 0f } }
    }

    @Test
    fun `aleatoire lance une erreur si la taille n'est pas positive`() {
        assertThatThrownBy { Population.aleatoire(taille = 0) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
