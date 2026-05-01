package com.chateaucombo.joueur.strategie.genetique.evolution

import com.chateaucombo.strategie.genetique.ExtracteurFeatures
import com.chateaucombo.strategie.genetique.Genome
import com.chateaucombo.strategie.genetique.evolution.Selection
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.Random

class SelectionTest {

    private fun genomeUniforme(valeur: Float): Genome =
        Genome(FloatArray(ExtracteurFeatures.NB_FEATURES) { valeur })

    @Test
    fun `tournoi est reproductible avec la meme graine`() {
        val genomes = List(10) { genomeUniforme(it.toFloat()) }
        val fitnesses = List(10) { it.toFloat() }

        val a = Selection.tournoi(genomes, fitnesses, k = 3, random = Random(7))
        val b = Selection.tournoi(genomes, fitnesses, k = 3, random = Random(7))

        assertThat(a).isEqualTo(b)
    }

    @Test
    fun `tournoi favorise les meilleurs sur de nombreux tirages`() {
        // 10 candidats avec fitness 0..9 ; 1000 tournois de taille 3 → la fitness moyenne du gagnant
        // doit être nettement supérieure à la moyenne brute (4.5).
        val genomes = List(10) { genomeUniforme(it.toFloat()) }
        val fitnesses = List(10) { it.toFloat() }
        val random = Random(2026)

        val moyenneGagnants = (1..1000)
            .map { Selection.tournoi(genomes, fitnesses, k = 3, random = random) }
            .map { it.poids[0] }
            .average()

        assertThat(moyenneGagnants).isGreaterThan(6.0)
    }

    @Test
    fun `tournoi lance une erreur si les listes ne sont pas alignees`() {
        assertThatThrownBy {
            Selection.tournoi(listOf(genomeUniforme(0f)), listOf(0f, 1f), k = 1)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `tournoi lance une erreur si k est hors bornes`() {
        val genomes = listOf(genomeUniforme(0f), genomeUniforme(1f))
        val fitnesses = listOf(0f, 1f)

        assertThatThrownBy { Selection.tournoi(genomes, fitnesses, k = 0) }
            .isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy { Selection.tournoi(genomes, fitnesses, k = 3) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
