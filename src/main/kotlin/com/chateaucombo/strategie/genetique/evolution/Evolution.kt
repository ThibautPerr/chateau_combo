package com.chateaucombo.strategie.genetique.evolution

import com.chateaucombo.strategie.genetique.Genome
import java.util.Random
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask

/**
 * Boucle évolutionnaire générationnelle :
 * - Évalue chaque génome via [Fitness] (évaluation parallélisée sur le ForkJoinPool commun).
 * - Conserve les meilleurs (`tauxElitisme`) tels quels d'une génération à l'autre.
 * - Remplit le reste de la nouvelle population par sélection par tournoi (k=`tournoiK`)
 *   + croisement uniforme + mutation gaussienne (sigma=`sigmaMutation`, taux=`tauxMutation`).
 *
 * Renvoie le meilleur génome jamais observé et l'historique des fitness par génération.
 */
class Evolution(
    val taillePopulation: Int = 60,
    val nbGenerations: Int = 30,
    val nbPartiesParEvaluation: Int = 100,
    val tournoiK: Int = 3,
    val sigmaInit: Float = 1.0f,
    val sigmaMutation: Float = 0.15f,
    val tauxMutation: Float = 0.15f,
    val tauxElitisme: Float = 0.1f,
    val random: Random = Random(),
    val fitness: Fitness = Fitness(nbPartiesParEvaluation),
    val surGeneration: (StatsGeneration) -> Unit = {},
) {

    data class StatsGeneration(
        val generation: Int,
        val meilleurFitness: Float,
        val moyenneFitness: Float,
        val medianeFitness: Float,
    )

    data class Resultat(
        val meilleurGenome: Genome,
        val meilleureFitness: Float,
        val historique: List<StatsGeneration>,
    )

    fun run(): Resultat {
        require(taillePopulation >= 2) { "La population doit contenir au moins 2 génomes" }
        require(nbGenerations >= 1) { "Il faut au moins 1 génération" }

        var population = Population.aleatoire(taillePopulation, sigmaInit, random)
        val historique = mutableListOf<StatsGeneration>()
        var meilleurGenome = population[0]
        var meilleureFitness = Float.NEGATIVE_INFINITY

        val nbElites = (taillePopulation * tauxElitisme).toInt().coerceAtLeast(1)

        for (generation in 1..nbGenerations) {
            val fitnesses = evalueParallele(population)

            val triCroissant = fitnesses.zip(population).sortedByDescending { it.first }
            val champion = triCroissant.first()
            if (champion.first > meilleureFitness) {
                meilleureFitness = champion.first
                meilleurGenome = champion.second
            }

            val stats = StatsGeneration(
                generation = generation,
                meilleurFitness = champion.first,
                moyenneFitness = fitnesses.average().toFloat(),
                medianeFitness = fitnesses.sorted()[fitnesses.size / 2],
            )
            historique += stats
            surGeneration(stats)

            if (generation == nbGenerations) break
            population = nouvelleGeneration(population, fitnesses, triCroissant, nbElites)
        }

        return Resultat(meilleurGenome, meilleureFitness, historique)
    }

    private fun evalueParallele(population: List<Genome>): List<Float> {
        val pool = ForkJoinPool.commonPool()
        val taches: List<ForkJoinTask<Float>> = population.map { genome ->
            pool.submit<Float> { fitness.evalue(genome) }
        }
        return taches.map { it.get() }
    }

    private fun nouvelleGeneration(
        population: List<Genome>,
        fitnesses: List<Float>,
        triCroissant: List<Pair<Float, Genome>>,
        nbElites: Int,
    ): List<Genome> {
        val nouvelle = triCroissant.take(nbElites).map { it.second }.toMutableList()
        while (nouvelle.size < taillePopulation) {
            val parentA = Selection.tournoi(population, fitnesses, tournoiK, random)
            val parentB = Selection.tournoi(population, fitnesses, tournoiK, random)
            val enfant = Croisement.mutation(
                Croisement.uniforme(parentA, parentB, random),
                sigmaMutation,
                tauxMutation,
                random,
            )
            nouvelle += enfant
        }
        return nouvelle
    }
}
