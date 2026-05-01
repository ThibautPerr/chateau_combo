package com.chateaucombo.strategie.genetique.evolution

import com.chateaucombo.joueur.Joueur
import com.chateaucombo.simulation.Simulation
import com.chateaucombo.strategie.StrategieAnticipatrice
import com.chateaucombo.strategie.StrategieGourmande
import com.chateaucombo.strategie.StrategiePrevoyante
import com.chateaucombo.strategie.genetique.Genome
import com.chateaucombo.strategie.genetique.StrategieGenetique
import java.nio.file.Path

/**
 * Mesure la qualité d'un génome en jouant `nbParties` parties contre les trois
 * stratégies heuristiques de référence (Gourmande, Prévoyante, Anticipatrice).
 *
 * La fitness retournée est l'**avantage moyen** : `score moyen du génétique`
 * moins la moyenne des scores moyens des trois adversaires. Une fitness positive
 * signifie qu'en moyenne le génome bat le panier de baselines.
 */
class Fitness(
    private val nbParties: Int = 100,
    private val pathCartes: Path = Path.of("src/main/resources/cartes"),
) {

    fun evalue(genome: Genome): Float {
        val joueurs = listOf(
            Joueur(id = 0, strategie = StrategieGenetique(genome)),
            Joueur(id = 1, strategie = StrategieGourmande()),
            Joueur(id = 2, strategie = StrategiePrevoyante()),
            Joueur(id = 3, strategie = StrategieAnticipatrice()),
        )
        val resultat = Simulation(joueurs, pathCartes).run(nbParties)
        val scoresJoueurs = resultat.joueurs.parJoueur
        val moyenneGenetique = scoresJoueurs[0].moyenne
        val moyenneAdversaires = scoresJoueurs.drop(1).map { it.moyenne }.average()
        return (moyenneGenetique - moyenneAdversaires).toFloat()
    }
}
