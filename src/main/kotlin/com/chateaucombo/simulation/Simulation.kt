package com.chateaucombo.simulation

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.chateaucombo.ChateauCombo
import com.chateaucombo.deck.repository.DeckRepository
import com.chateaucombo.effet.model.ScoreContext
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

    fun run(nbParties: Int): ResultatSimulation {
        val scoresParJoueur = List(nbJoueurs) { mutableListOf<Int>() }
        val scoresJoueurParCarte = mutableMapOf<String, MutableList<Int>>()
        val scoresCarteParCarte = mutableMapOf<String, MutableList<Int>>()

        silenceLogs {
            repeat(nbParties) {
                val joueurs = List(nbJoueurs) { Joueur(id = it) }
                jeu.play(joueurs, pathCartes)
                joueurs.forEachIndexed { index, joueur ->
                    scoresParJoueur[index].add(joueur.score)
                    joueur.tableau.cartesPositionees.forEach { cartePositionee ->
                        val context = ScoreContext(joueurActuel = joueur, joueurs = joueurs, cartePositionee = cartePositionee)
                        val scoreEffet = cartePositionee.carte.effetScore.score(context)
                        val scoreBourse = cartePositionee.carte.bourse?.orDepose?.times(2) ?: 0
                        val nom = if (cartePositionee.carte is com.chateaucombo.deck.model.CarteVerso) "Carte Verso" else cartePositionee.carte.nom
                        scoresJoueurParCarte.getOrPut(nom) { mutableListOf() }.add(joueur.score)
                        scoresCarteParCarte.getOrPut(nom) { mutableListOf() }.add(scoreEffet + scoreBourse)
                    }
                }
            }
        }

        val tousLesScores = scoresParJoueur.flatten()
        return ResultatSimulation(
            joueurs = StatistiquesSimulation(
                nbParties = nbParties,
                nbJoueurs = nbJoueurs,
                global = tousLesScores.stats(),
                parJoueur = scoresParJoueur.mapIndexed { index, scores ->
                    StatistiquesJoueur(
                        joueurId = index,
                        moyenne = scores.average().round2(),
                        premierQuartile = scores.percentile(25.0),
                        mediane = scores.percentile(50.0),
                        troisiemeQuartile = scores.percentile(75.0),
                    )
                },
            ),
            cartes = scoresJoueurParCarte.keys.sorted().map { nom ->
                StatistiquesCarte(
                    nomCarte = nom,
                    scoreJoueur = scoresJoueurParCarte[nom]!!.stats(),
                    scoreCarte = scoresCarteParCarte[nom]!!.stats(),
                )
            },
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
            moyenne = average().round2(),
            premierQuartile = percentile(25.0),
            mediane = percentile(50.0),
            troisiemeQuartile = percentile(75.0),
        )

    private fun Double.round2() = kotlin.math.round(this * 100) / 100.0

    private fun List<Int>.percentile(p: Double): Double {
        val sorted = sorted()
        val index = p / 100.0 * (sorted.size - 1)
        val lo = index.toInt()
        val hi = minOf(lo + 1, sorted.size - 1)
        return sorted[lo] + (index - lo) * (sorted[hi] - sorted[lo])
    }
}
