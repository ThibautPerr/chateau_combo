package com.chateaucombo

import com.chateaucombo.simulation.Simulation
import tools.jackson.module.kotlin.jacksonMapperBuilder
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main(args: Array<String>) {
    val nbParties = args.firstOrNull()?.toIntOrNull() ?: 10000
    println("Simulation de $nbParties parties en cours...")

    val simulation = Simulation()
    val resultat = simulation.run(nbParties)

    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm"))
    val runDir = File("stats", timestamp).also { it.mkdirs() }

    val playerFile = File(runDir, "player_scores.json")
    resultat.joueurs.writeIn(playerFile)
    println("Résultats joueurs écrits dans ${playerFile.absolutePath}")

    val cardFile = File(runDir, "card_scores.json")
    resultat.cartes.writeIn(cardFile)
    println("Résultats cartes écrits dans ${cardFile.absolutePath}")

    val global = resultat.joueurs.global
    println(
        "Global — moyenne: ${"%.1f".format(global.moyenne)}, " +
                "Q1: ${"%.1f".format(global.premierQuartile)}, " +
                "médiane: ${"%.1f".format(global.mediane)}, " +
                "Q3: ${"%.1f".format(global.troisiemeQuartile)}"
    )
}

private val mapper = jacksonMapperBuilder().build().writerWithDefaultPrettyPrinter()

private fun Any.writeIn(outputFile: File) {
    mapper.writeValue(outputFile, this)
}
