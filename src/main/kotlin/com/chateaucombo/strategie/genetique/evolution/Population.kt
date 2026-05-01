package com.chateaucombo.strategie.genetique.evolution

import com.chateaucombo.strategie.genetique.ExtracteurFeatures
import com.chateaucombo.strategie.genetique.Genome
import java.util.Random

/**
 * Initialisation aléatoire d'une population de génomes. Les poids sont tirés
 * d'une distribution gaussienne `N(0, sigmaInit²)`. L'aléa est injectable
 * pour rendre la génération reproductible dans les tests.
 */
object Population {

    fun aleatoire(taille: Int, sigmaInit: Float = 1.0f, random: Random = Random()): List<Genome> {
        require(taille > 0) { "La taille de la population doit être strictement positive (reçu $taille)" }
        return List(taille) {
            Genome(FloatArray(ExtracteurFeatures.NB_FEATURES) { (random.nextGaussian() * sigmaInit).toFloat() })
        }
    }
}
