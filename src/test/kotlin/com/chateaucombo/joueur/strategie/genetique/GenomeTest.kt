package com.chateaucombo.joueur.strategie.genetique

import com.chateaucombo.strategie.genetique.ExtracteurFeatures
import com.chateaucombo.strategie.genetique.Genome
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class GenomeTest {

    @Test
    fun `genome par defaut a la bonne taille`() {
        val genome = Genome.parDefaut()

        assertThat(genome.poids).hasSize(ExtracteurFeatures.NB_FEATURES)
    }

    @Test
    fun `lance une erreur si la taille du vecteur de poids est incorrecte`() {
        assertThatThrownBy { Genome(FloatArray(ExtracteurFeatures.NB_FEATURES - 1)) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining(ExtracteurFeatures.NB_FEATURES.toString())
    }

    @Test
    fun `score est le produit scalaire poids vs features`() {
        val poids = FloatArray(ExtracteurFeatures.NB_FEATURES) { 2f }
        val features = FloatArray(ExtracteurFeatures.NB_FEATURES) { 3f }
        val genome = Genome(poids)

        val score = genome.score(features)

        assertThat(score).isEqualTo(2f * 3f * ExtracteurFeatures.NB_FEATURES)
    }

    @Test
    fun `score lance une erreur si la taille du vecteur de features est incorrecte`() {
        val genome = Genome(FloatArray(ExtracteurFeatures.NB_FEATURES))

        assertThatThrownBy { genome.score(FloatArray(ExtracteurFeatures.NB_FEATURES - 1)) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `equals compare les poids contenu par contenu`() {
        val a = Genome(FloatArray(ExtracteurFeatures.NB_FEATURES) { it.toFloat() })
        val b = Genome(FloatArray(ExtracteurFeatures.NB_FEATURES) { it.toFloat() })
        val c = Genome(FloatArray(ExtracteurFeatures.NB_FEATURES) { (it + 1).toFloat() })

        assertThat(a).isEqualTo(b)
        assertThat(a.hashCode()).isEqualTo(b.hashCode())
        assertThat(a).isNotEqualTo(c)
    }

    @Test
    fun `peut etre serialise et deserialise via JSON`(@TempDir dir: Path) {
        val original = Genome(FloatArray(ExtracteurFeatures.NB_FEATURES) { it * 0.5f })
        val fichier = File(dir.toFile(), "genome.json")

        original.ecritDans(fichier)
        val recharge = Genome.depuis(fichier)

        assertThat(recharge).isEqualTo(original)
    }
}
