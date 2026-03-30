package com.chateaucombo

import com.chateaucombo.simulation.Simulation
import com.chateaucombo.simulation.StatistiquesSimulation
import tools.jackson.module.kotlin.jacksonMapperBuilder
import java.io.File
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main(args: Array<String>) {
    val nbParties = args.firstOrNull()?.toIntOrNull() ?: 10000
    println("Simulation de $nbParties parties en cours...")

    val simulation = Simulation()
    val stats = simulation.run(nbParties)

    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm"))
    val statsDir = Path.of("stats").toFile().also { it.mkdirs() }
    val outputFile = File(statsDir, "player_scores_$timestamp.json")

    stats.writeIn(outputFile)

    println("Résultats écrits dans ${outputFile.absolutePath}")
    println(
        "Global — moyenne: ${"%.1f".format(stats.global.moyenne)}, " +
                "Q1: ${"%.1f".format(stats.global.premierQuartile)}, " +
                "médiane: ${"%.1f".format(stats.global.mediane)}, " +
                "Q3: ${"%.1f".format(stats.global.troisiemeQuartile)}"
    )
}

private fun StatistiquesSimulation.writeIn(outputFile: File) {
    jacksonMapperBuilder().build()
        .writerWithDefaultPrettyPrinter()
        .writeValue(outputFile, this)
}
