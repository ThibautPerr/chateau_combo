package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.Chatelain
import com.chateaucombo.joueur.model.Joueur
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteCleParChatelainDansTableauVoisin")
class AjouteCleParChatelainDansTableauVoisin : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val voisins = voisins(context)
        if (voisins.isNotEmpty()) {
            val (voisinId, cle) = voisins
                .map { voisin -> voisin.id to voisin.nbChatelains() }
                .maxBy { (_, cle) -> cle }
            logger.info { "Ajout de $cle clés au joueur ${context.joueurActuel.id} (tableau du voisin $voisinId)" }
            context.joueurActuel.cle += cle
        }
    }

    private fun Joueur.nbChatelains(): Int =
        this.tableau.cartesPositionees.count { it.carte is Chatelain }

    private fun voisins(context: EffetContext): List<Joueur> {
        val joueurs = context.joueurs
        if (joueurs.size <= 1) return emptyList()
        val index = joueurs.indexOf(context.joueurActuel)
        val precedent = joueurs[(index - 1 + joueurs.size) % joueurs.size]
        val suivant = joueurs[(index + 1) % joueurs.size]
        return listOf(precedent, suivant).distinct()
    }
}
