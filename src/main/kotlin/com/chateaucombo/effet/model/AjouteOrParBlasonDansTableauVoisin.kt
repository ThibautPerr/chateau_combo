package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Blason
import com.chateaucombo.joueur.model.Joueur
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteOrParBlasonDansTableauVoisin")
data class AjouteOrParBlasonDansTableauVoisin(val blason: Blason) : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val voisins = voisins(context)
        if (voisins.isNotEmpty()) {
            val (voisinId, or) = voisins
                .map { voisin -> voisin.id to voisin.occurrencesDuBlason() }
                .maxBy { (_, or) -> or }
            logger.info { "Ajout de $or or au joueur ${context.joueurActuel.id} (tableau du voisin ${voisinId})" }
            context.joueurActuel.or += or
        }
    }

    private fun Joueur.occurrencesDuBlason(): Int =
        this.tableau.cartesPositionees.sumOf { cartePositionee -> cartePositionee.carte.blasons.count { cardBlason -> cardBlason == blason } }

    private fun voisins(context: EffetContext): List<Joueur> {
        val joueurs = context.joueurs
        if (joueurs.size <= 1) return emptyList()
        val index = joueurs.indexOf(context.joueurActuel)
        val precedent = joueurs[(index - 1 + joueurs.size) % joueurs.size]
        val suivant = joueurs[(index + 1) % joueurs.size]
        return listOf(precedent, suivant).distinct()
    }
}
