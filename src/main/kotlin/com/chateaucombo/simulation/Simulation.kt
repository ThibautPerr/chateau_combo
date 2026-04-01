package com.chateaucombo.simulation

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.chateaucombo.ChateauCombo
import com.chateaucombo.deck.model.CarteVerso
import com.chateaucombo.deck.repository.DeckRepository
import com.chateaucombo.effet.model.EffetScoreVide
import com.chateaucombo.effet.model.ScoreContext
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.joueur.repository.JoueurRepository
import com.chateaucombo.simulation.StatistiquesSimulation.StatistiquesJoueur
import com.chateaucombo.simulation.StatistiquesSimulation.StatistiquesPoints
import com.chateaucombo.tableau.model.CartePositionee
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
        val acc = Accumulateurs(nbJoueurs)
        silenceLogs {
            repeat(nbParties) {
                val joueurs = List(nbJoueurs) { Joueur(id = it) }
                jeu.play(joueurs, pathCartes)
                accumuleDonneesPartie(joueurs, acc)
            }
        }
        return construitResultat(nbParties, acc)
    }

    private fun accumuleDonneesPartie(joueurs: List<Joueur>, acc: Accumulateurs) {
        joueurs.forEachIndexed { index, joueur ->
            acc.scoresParJoueur[index].add(joueur.score)
            joueur.tableau.cartesPositionees.forEach { cartePositionee ->
                accumuleDonneesCarte(joueur, joueurs, cartePositionee, acc)
            }
        }
    }

    private fun accumuleDonneesCarte(
        joueur: Joueur,
        joueurs: List<Joueur>,
        cartePositionee: CartePositionee,
        acc: Accumulateurs,
    ) {
        val carte = cartePositionee.carte
        val context = ScoreContext(joueurActuel = joueur, joueurs = joueurs, cartePositionee = cartePositionee)
        val cardScore = carte.effetScore.score(context) + (carte.bourse?.orDepose?.times(2) ?: 0)
        val nom = if (carte is CarteVerso) "Carte Verso" else carte.nom

        acc.scoresJoueurParCarte.getOrPut(nom) { mutableListOf() }.add(joueur.score)
        acc.scoresCarteParCarte.getOrPut(nom) { mutableListOf() }.add(cardScore)

        val effetTypes = carte.effets.effets.map { it::class.simpleName!! } +
                carte.effets.effetsPassifs.map { it::class.simpleName!! }
        val effetScoreType = carte.effetScore::class.simpleName!!

        acc.effetsParCarte.putIfAbsent(nom, effetTypes)
        acc.effetScoreParCarte.putIfAbsent(nom, effetScoreType)

        for (type in effetTypes) {
            acc.scoresJoueurParEffet.getOrPut(type) { mutableListOf() }.add(joueur.score)
            acc.scoresCarteParEffet.getOrPut(type) { mutableListOf() }.add(cardScore)
        }
        if (carte.effetScore !is EffetScoreVide) {
            acc.scoresJoueurParEffetScore.getOrPut(effetScoreType) { mutableListOf() }.add(joueur.score)
            acc.scoresCarteParEffetScore.getOrPut(effetScoreType) { mutableListOf() }.add(cardScore)
        }
    }

    private fun inverseEffetsParCarte(acc: Accumulateurs): Map<String, Set<String>> {
        val cartesParEffet = mutableMapOf<String, MutableSet<String>>()
        for ((nom, types) in acc.effetsParCarte) {
            for (type in types) cartesParEffet.getOrPut(type) { mutableSetOf() }.add(nom)
        }
        return cartesParEffet
    }

    private fun inverseEffetScoreParCarte(acc: Accumulateurs): Map<String, Set<String>> {
        val cartesParEffetScore = mutableMapOf<String, MutableSet<String>>()
        for ((nom, type) in acc.effetScoreParCarte) {
            if (type != "EffetScoreVide") cartesParEffetScore.getOrPut(type) { mutableSetOf() }.add(nom)
        }
        return cartesParEffetScore
    }

    private fun construitResultat(nbParties: Int, acc: Accumulateurs): ResultatSimulation {
        val tousLesScores = acc.scoresParJoueur.flatten()
        val cartesParEffet = inverseEffetsParCarte(acc)
        val cartesParEffetScore = inverseEffetScoreParCarte(acc)

        return ResultatSimulation(
            joueurs = StatistiquesSimulation(
                nbParties = nbParties,
                nbJoueurs = nbJoueurs,
                global = tousLesScores.stats(),
                parJoueur = acc.scoresParJoueur.mapIndexed { index, scores ->
                    StatistiquesJoueur(
                        joueurId = index,
                        moyenne = scores.average().round2(),
                        premierQuartile = scores.percentile(25.0),
                        mediane = scores.percentile(50.0),
                        troisiemeQuartile = scores.percentile(75.0),
                    )
                },
            ),
            cartes = acc.scoresJoueurParCarte.keys.sorted().map { nom ->
                StatistiquesCarte(
                    nomCarte = nom,
                    effets = acc.effetsParCarte[nom] ?: emptyList(),
                    effetScore = acc.effetScoreParCarte[nom] ?: "EffetScoreVide",
                    scoreJoueur = acc.scoresJoueurParCarte[nom]!!.stats(),
                    scoreCarte = acc.scoresCarteParCarte[nom]!!.stats(),
                )
            },
            parEffet = acc.scoresJoueurParEffet.keys.sorted().map { type ->
                StatistiquesEffet(
                    effet = type,
                    cartes = cartesParEffet[type]!!.sorted(),
                    scoreJoueur = acc.scoresJoueurParEffet[type]!!.stats(),
                    scoreCarte = acc.scoresCarteParEffet[type]!!.stats(),
                )
            },
            parEffetScore = acc.scoresJoueurParEffetScore.keys.sorted().map { type ->
                StatistiquesEffet(
                    effet = type,
                    cartes = cartesParEffetScore[type]!!.sorted(),
                    scoreJoueur = acc.scoresJoueurParEffetScore[type]!!.stats(),
                    scoreCarte = acc.scoresCarteParEffetScore[type]!!.stats(),
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

private class Accumulateurs(nbJoueurs: Int) {
    val scoresParJoueur: List<MutableList<Int>> = List(nbJoueurs) { mutableListOf() }
    val scoresJoueurParCarte: MutableMap<String, MutableList<Int>> = mutableMapOf()
    val scoresCarteParCarte: MutableMap<String, MutableList<Int>> = mutableMapOf()
    val effetsParCarte: MutableMap<String, List<String>> = mutableMapOf()
    val effetScoreParCarte: MutableMap<String, String> = mutableMapOf()
    val scoresJoueurParEffet: MutableMap<String, MutableList<Int>> = mutableMapOf()
    val scoresCarteParEffet: MutableMap<String, MutableList<Int>> = mutableMapOf()
    val scoresJoueurParEffetScore: MutableMap<String, MutableList<Int>> = mutableMapOf()
    val scoresCarteParEffetScore: MutableMap<String, MutableList<Int>> = mutableMapOf()
}
