package com.chateaucombo.joueur.strategie.genetique.evolution

import com.chateaucombo.strategie.genetique.ExtracteurFeatures
import com.chateaucombo.strategie.genetique.Genome
import com.chateaucombo.strategie.genetique.evolution.Croisement
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.Random

class CroisementTest {

    private fun genomeRempli(valeur: Float): Genome =
        Genome(FloatArray(ExtracteurFeatures.NB_FEATURES) { valeur })

    @Test
    fun `uniforme produit un genome de meme taille`() {
        val a = genomeRempli(1f)
        val b = genomeRempli(-1f)

        val enfant = Croisement.uniforme(a, b, Random(0))

        assertThat(enfant.poids).hasSize(ExtracteurFeatures.NB_FEATURES)
    }

    @Test
    fun `uniforme entre deux parents identiques produit le meme genome`() {
        val parent = genomeRempli(2f)

        val enfant = Croisement.uniforme(parent, parent, Random(0))

        assertThat(enfant).isEqualTo(parent)
    }

    @Test
    fun `uniforme produit chaque gene a partir d'un parent`() {
        val a = genomeRempli(1f)
        val b = genomeRempli(-1f)

        val enfant = Croisement.uniforme(a, b, Random(42))

        assertThat(enfant.poids.toList()).allMatch { it == 1f || it == -1f }
    }

    @Test
    fun `uniforme est reproductible avec la meme graine`() {
        val a = genomeRempli(1f)
        val b = genomeRempli(-1f)

        val premier = Croisement.uniforme(a, b, Random(123))
        val second = Croisement.uniforme(a, b, Random(123))

        assertThat(premier).isEqualTo(second)
    }

    @Test
    fun `mutation avec taux 0 ne modifie pas le genome`() {
        val genome = genomeRempli(1f)

        val mute = Croisement.mutation(genome, sigma = 1f, taux = 0f, random = Random(0))

        assertThat(mute).isEqualTo(genome)
    }

    @Test
    fun `mutation avec sigma 0 ne modifie pas le genome`() {
        val genome = genomeRempli(1f)

        val mute = Croisement.mutation(genome, sigma = 0f, taux = 1f, random = Random(0))

        assertThat(mute).isEqualTo(genome)
    }

    @Test
    fun `mutation avec taux 1 modifie tous les genes`() {
        val genome = genomeRempli(0f)

        val mute = Croisement.mutation(genome, sigma = 1f, taux = 1f, random = Random(0))

        assertThat(mute.poids.toList()).noneMatch { it == 0f }
    }

    @Test
    fun `mutation lance une erreur sur un sigma negatif`() {
        assertThatThrownBy {
            Croisement.mutation(genomeRempli(0f), sigma = -1f, taux = 0.1f, random = Random(0))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `mutation lance une erreur sur un taux hors bornes`() {
        assertThatThrownBy {
            Croisement.mutation(genomeRempli(0f), sigma = 1f, taux = -0.1f, random = Random(0))
        }.isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy {
            Croisement.mutation(genomeRempli(0f), sigma = 1f, taux = 1.5f, random = Random(0))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}
