package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteOrDansBourses")
data class AjouteOrDansBourses(val or: Int) : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val orDeposeTotal = context.joueurActuel.tableau.cartesPositionees
            .mapNotNull { cartePositionee -> cartePositionee.carte.bourse }
            .filter { bourseScore -> bourseScore.orDepose < bourseScore.taille }
            .sumOf { bourse ->
                val orAjoute = minOf(or, bourse.taille - bourse.orDepose)
                bourse.orDepose += orAjoute
                orAjoute
            }
        logger.info { "Joueur ${context.joueurActuel.id} ajoute $orDeposeTotal or dans ses bourses" }
    }
}
