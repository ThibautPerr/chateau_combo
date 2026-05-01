package com.chateaucombo

import ch.qos.logback.classic.Logger
import com.chateaucombo.strategie.genetique.evolution.Evolution
import com.chateaucombo.strategie.genetique.evolution.Fitness
import org.slf4j.LoggerFactory
import tools.jackson.module.kotlin.jacksonMapperBuilder
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Random

/**
 * Entrée pour entraîner un génome via l'algorithme évolutionnaire.
 *
 * Arguments positionnels (tous optionnels) :
 *   1. taillePopulation (défaut 60)
 *   2. nbGenerations (défaut 30)
 *   3. nbPartiesParEvaluation (défaut 100)
 *   4. graine RNG (défaut horloge)
 *
 * Sortie :
 *   - stats/genomes/best.json : meilleur génome (réutilisé automatiquement par MainKt)
 *   - stats/genomes/<timestamp>.json : archive du même génome
 *   - stats/genomes/<timestamp>_historique.json : historique de fitness par génération
 */
fun main(args: Array<String>) {
    forceInitialisationSlf4j()
    val taillePopulation = args.getOrNull(0)?.toIntOrNull() ?: 60
    val nbGenerations = args.getOrNull(1)?.toIntOrNull() ?: 30
    val nbPartiesParEvaluation = args.getOrNull(2)?.toIntOrNull() ?: 100
    val graine = args.getOrNull(3)?.toLongOrNull() ?: System.currentTimeMillis()

    println("Évolution génétique : $taillePopulation génomes × $nbGenerations générations × $nbPartiesParEvaluation parties (graine=$graine)")
    val debut = System.currentTimeMillis()
    val evolution = Evolution(
        taillePopulation = taillePopulation,
        nbGenerations = nbGenerations,
        nbPartiesParEvaluation = nbPartiesParEvaluation,
        random = Random(graine),
        fitness = Fitness(nbParties = nbPartiesParEvaluation),
        surGeneration = { stats ->
            println(
                "Gen %2d : meilleur=%.2f moyenne=%.2f médiane=%.2f"
                    .format(stats.generation, stats.meilleurFitness, stats.moyenneFitness, stats.medianeFitness)
            )
        },
    )
    val resultat = evolution.run()
    val duree = (System.currentTimeMillis() - debut) / 1000.0
    println("Évolution terminée en ${"%.1f".format(duree)}s — meilleure fitness = ${"%.2f".format(resultat.meilleureFitness)}")

    val genomesDir = File("stats/genomes").also { it.mkdirs() }
    val horodatage = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm"))
    val fichierBest = File(genomesDir, "best.json")
    val fichierArchive = File(genomesDir, "$horodatage.json")
    val fichierHistorique = File(genomesDir, "${horodatage}_historique.json")

    resultat.meilleurGenome.ecritDans(fichierBest)
    resultat.meilleurGenome.ecritDans(fichierArchive)
    mapper.writeValue(fichierHistorique, resultat.historique)

    println("Génome sauvegardé dans ${fichierBest.absolutePath}")
    println("Archive : ${fichierArchive.absolutePath}")
    println("Historique : ${fichierHistorique.absolutePath}")
}

private val mapper = jacksonMapperBuilder().build().writerWithDefaultPrettyPrinter()

/**
 * Force la fin de l'initialisation SLF4J sur le thread principal pour minimiser le
 * nombre de Simulation parallèles qui démarrent pendant l'init et n'arrivent pas à
 * caster le root logger. Boucle courte (max ~1s) jusqu'à ce que le cast soit valide.
 */
private fun forceInitialisationSlf4j() {
    repeat(100) {
        if (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) is Logger) return
        Thread.sleep(10)
    }
}
