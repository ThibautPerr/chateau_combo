package com.chateaucombo.strategie.genetique.evolution

import com.chateaucombo.strategie.genetique.Genome
import java.util.Random

/**
 * Opérateurs de variation : croisement uniforme (chaque gène vient au hasard
 * de l'un des deux parents) et mutation gaussienne (chaque gène est perturbé
 * avec probabilité `taux` par un bruit `N(0, sigma²)`).
 */
object Croisement {

    fun uniforme(parentA: Genome, parentB: Genome, random: Random = Random()): Genome {
        require(parentA.poids.size == parentB.poids.size) {
            "Les deux parents doivent avoir la même taille de génome"
        }
        val poids = FloatArray(parentA.poids.size) { i ->
            if (random.nextBoolean()) parentA.poids[i] else parentB.poids[i]
        }
        return Genome(poids)
    }

    fun mutation(
        genome: Genome,
        sigma: Float,
        taux: Float,
        random: Random = Random(),
    ): Genome {
        require(sigma >= 0f) { "sigma doit être positif (reçu $sigma)" }
        require(taux in 0f..1f) { "taux doit être dans [0, 1] (reçu $taux)" }
        val poids = FloatArray(genome.poids.size) { i ->
            if (random.nextDouble() < taux) {
                genome.poids[i] + (random.nextGaussian() * sigma).toFloat()
            } else {
                genome.poids[i]
            }
        }
        return Genome(poids)
    }
}
