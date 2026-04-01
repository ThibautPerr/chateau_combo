package com.chateaucombo

import com.chateaucombo.simulation.ResultatSimulation
import com.chateaucombo.simulation.Simulation
import tools.jackson.module.kotlin.jacksonMapperBuilder
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main(args: Array<String>) {
    val nbParties = args.firstOrNull()?.toIntOrNull() ?: 10000
    println("Simulation de $nbParties parties en cours...")
    val resultat = Simulation().run(nbParties)
    val runDir = File("stats", timestamp()).also { it.mkdirs() }
    ecritsResultats(resultat, runDir)
    metsAJourIndex(File("stats"))
    afficheResume(resultat)
}

private fun timestamp() = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm"))

private fun ecritsResultats(resultat: ResultatSimulation, runDir: File) {
    resultat.joueurs.writeIn(File(runDir, "player_scores.json"))
    resultat.cartes.writeIn(File(runDir, "card_scores.json"))
    resultat.parEffet.writeIn(File(runDir, "effect_scores.json"))
    resultat.parEffetScore.writeIn(File(runDir, "score_effect_scores.json"))
    println("Résultats écrits dans ${runDir.absolutePath}")
}

private fun metsAJourIndex(statsDir: File) {
    val runs = statsDir.listFiles()
        ?.filter { it.isDirectory }
        ?.map { it.name }
        ?.sortedDescending()
        ?: emptyList()
    mapper.writeValue(File(statsDir, "runs.json"), runs)
    println("Index des simulations mis à jour (${runs.size} runs)")
}

private fun afficheResume(resultat: ResultatSimulation) {
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
