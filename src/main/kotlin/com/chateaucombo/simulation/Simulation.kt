package com.chateaucombo.simulation

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.chateaucombo.ChateauCombo
import com.chateaucombo.deck.repository.DeckRepository
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.joueur.repository.JoueurRepository
import com.chateaucombo.simulation.StatistiquesSimulation.StatistiquesJoueur
import com.chateaucombo.simulation.StatistiquesSimulation.StatistiquesPoints
import com.chateaucombo.tableau.repository.TableauRepository
import org.slf4j.LoggerFactory
import java.nio.file.Path

class Simulation(
    private val nbJoueurs: Int = 4,
    private val pathCartes: Path = Path.of("src/main/resources/cartes"),
) {
    private val deckRepository = DeckRepository()
    private val jeu = ChateauCombo(
        joueurRepository = JoueurRepository(TableauRepository(), deckRepository),
        deckRepository = deckRepository,
    )

    fun run(nbParties: Int): StatistiquesSimulation {
        val scoresParJoueur = List(nbJoueurs) { mutableListOf<Int>() }

        silenceLogs {
            repeat(nbParties) {
                val joueurs = List(nbJoueurs) { Joueur(id = it) }
                jeu.play(joueurs, pathCartes)
                joueurs.forEachIndexed { index, joueur ->
                    scoresParJoueur[index].add(joueur.score)
                }
            }
        }

        val tousLesScores = scoresParJoueur.flatten()
        return StatistiquesSimulation(
            nbParties = nbParties,
            nbJoueurs = nbJoueurs,
            global = tousLesScores.stats(),
            parJoueur = scoresParJoueur.mapIndexed { index, scores ->
                StatistiquesJoueur(
                    joueurId = index,
                    moyenne = scores.average(),
                    premierQuartile = scores.percentile(25.0),
                    mediane = scores.percentile(50.0),
                    troisiemeQuartile = scores.percentile(75.0),
                )
            }
        )
    }

    private fun silenceLogs(block: () -> Unit) {
        val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        val originalLevel = rootLogger.level
        rootLogger.level = Level.WARN
        try {
            block()
        } finally {
            rootLogger.level = originalLevel
        }
    }

    private fun List<Int>.stats() =
        StatistiquesPoints(
            moyenne = average(),
            premierQuartile = percentile(25.0),
            mediane = percentile(50.0),
            troisiemeQuartile = percentile(75.0),
        )

    private fun List<Int>.percentile(p: Double): Double {
        val sorted = sorted()
        val index = p / 100.0 * (sorted.size - 1)
        val lo = index.toInt()
        val hi = minOf(lo + 1, sorted.size - 1)
        return sorted[lo] + (index - lo) * (sorted[hi] - sorted[lo])
    }
}
