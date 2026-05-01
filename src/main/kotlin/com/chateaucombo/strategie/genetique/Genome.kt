package com.chateaucombo.strategie.genetique

import tools.jackson.module.kotlin.jacksonMapperBuilder
import java.io.File

/**
 * Vecteur de poids appris d'une stratégie génétique.
 *
 * Le score d'un coup est le produit scalaire `poids · features` calculé
 * par [ExtracteurFeatures]. La taille du vecteur doit correspondre exactement
 * à [ExtracteurFeatures.NB_FEATURES].
 */
data class Genome(val poids: FloatArray) {

    init {
        require(poids.size == ExtracteurFeatures.NB_FEATURES) {
            "Le génome doit contenir ${ExtracteurFeatures.NB_FEATURES} poids (reçu ${poids.size})"
        }
    }

    fun score(features: FloatArray): Float {
        require(features.size == poids.size) {
            "Le vecteur de features doit contenir ${poids.size} valeurs (reçu ${features.size})"
        }
        var total = 0f
        for (i in poids.indices) total += poids[i] * features[i]
        return total
    }

    fun ecritDans(fichier: File) {
        mapper.writeValue(fichier, this)
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is Genome && poids.contentEquals(other.poids))

    override fun hashCode(): Int = poids.contentHashCode()

    companion object {
        private val mapper = jacksonMapperBuilder().build()

        /**
         * Génome de départ approximant grossièrement [com.chateaucombo.strategie.StrategieGourmande] :
         * récompense le gain de score et la valeur des effets de placement, pénalise le coût
         * d'opportunité de l'or et la dépense d'une clé. Les autres dimensions sont neutres
         * et n'attendent que d'être ajustées par l'évolution.
         */
        fun parDefaut(): Genome = Genome(
            floatArrayOf(
                1.0f,   // 0 - gain de score final théorique
                1.0f,   // 1 - valeur estimée des effets de placement
                -1.0f,  // 2 - coût d'opportunité de l'or dépensé
                0.0f,   // 3 - or restant après achat
                0.0f,   // 4 - carte chatelain
                0.0f,   // 5 - carte villageois
                0.0f,   // 6 - carte verso
                0.0f,   // 7 - position coin
                0.0f,   // 8 - position bord
                0.0f,   // 9 - position centre
                0.0f,   // 10 - blasons identiques déjà en rangée
                0.0f,   // 11 - blasons identiques déjà en colonne
                0.0f,   // 12 - blasons distincts après placement
                0.0f,   // 13 - tour normalisé
                -1.0f,  // 14 - pénalité d'utilisation d'une clé
            )
        )

        fun depuis(fichier: File): Genome = mapper.readValue(fichier, Genome::class.java)
    }
}
