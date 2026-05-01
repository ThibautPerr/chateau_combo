package com.chateaucombo.joueur.strategie.genetique.evolution

import com.chateaucombo.strategie.genetique.ExtracteurFeatures
import com.chateaucombo.strategie.genetique.evolution.Evolution
import com.chateaucombo.strategie.genetique.evolution.Fitness
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Random

class EvolutionTest {

    @Test
    fun `run retourne un genome valide et un historique de la bonne longueur`() {
        // Smoke test : population minimale, peu de parties, peu de générations.
        val evolution = Evolution(
            taillePopulation = 4,
            nbGenerations = 2,
            nbPartiesParEvaluation = 5,
            random = Random(2026),
            fitness = Fitness(nbParties = 5),
        )

        val resultat = evolution.run()

        assertThat(resultat.meilleurGenome.poids).hasSize(ExtracteurFeatures.NB_FEATURES)
        assertThat(resultat.historique).hasSize(2)
        assertThat(resultat.historique.map { it.generation }).containsExactly(1, 2)
        assertThat(resultat.meilleureFitness).isFinite()
    }

    @Test
    fun `meilleureFitness est la max de toutes les generations`() {
        val evolution = Evolution(
            taillePopulation = 4,
            nbGenerations = 2,
            nbPartiesParEvaluation = 5,
            random = Random(7),
            fitness = Fitness(nbParties = 5),
        )

        val resultat = evolution.run()

        assertThat(resultat.meilleureFitness)
            .isGreaterThanOrEqualTo(resultat.historique.maxOf { it.meilleurFitness })
    }
}
