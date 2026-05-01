package com.chateaucombo.strategie.genetique.evolution

import com.chateaucombo.strategie.genetique.Genome
import java.util.Random

/**
 * Sélection par tournoi : tire `k` génomes au hasard (avec remise) et garde le
 * meilleur selon la fitness fournie. Plus `k` est grand, plus la pression de
 * sélection est forte (k=1 = aléatoire pur, k=taille = élitisme global).
 */
object Selection {

    fun tournoi(
        genomes: List<Genome>,
        fitnesses: List<Float>,
        k: Int,
        random: Random = Random(),
    ): Genome {
        require(genomes.size == fitnesses.size) {
            "Les listes doivent avoir la même taille (${genomes.size} vs ${fitnesses.size})"
        }
        require(genomes.isNotEmpty()) { "La population doit contenir au moins un génome" }
        require(k in 1..genomes.size) { "k doit être dans [1, ${genomes.size}] (reçu $k)" }

        var meilleurIndex = -1
        var meilleureFitness = Float.NEGATIVE_INFINITY
        repeat(k) {
            val idx = random.nextInt(genomes.size)
            if (fitnesses[idx] > meilleureFitness) {
                meilleureFitness = fitnesses[idx]
                meilleurIndex = idx
            }
        }
        return genomes[meilleurIndex]
    }
}
