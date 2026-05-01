package com.chateaucombo.joueur.strategie.genetique.evolution

import com.chateaucombo.strategie.genetique.Genome
import com.chateaucombo.strategie.genetique.evolution.Fitness
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FitnessTest {

    @Test
    fun `evalue retourne un nombre fini pour le genome par defaut`() {
        val fitness = Fitness(nbParties = 5)

        val score = fitness.evalue(Genome.parDefaut())

        assertThat(score).isFinite()
    }

    @Test
    fun `evalue est plus eleve pour le genome par defaut que pour un genome nul`() {
        // Smoke test : sur 30 parties, le génome par défaut (qui mime Gourmande) doit
        // se comporter mieux qu'un génome de poids nuls (qui choisit aléatoirement entre
        // coups équivalents — pratiquement aléatoire).
        val fitness = Fitness(nbParties = 30)

        val scoreDefaut = fitness.evalue(Genome.parDefaut())
        val scoreNul = fitness.evalue(Genome(FloatArray(Genome.parDefaut().poids.size)))

        assertThat(scoreDefaut).isGreaterThan(scoreNul)
    }
}
