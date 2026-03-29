package com.chateaucombo.effet.model

import com.chateaucombo.deck.model.Chatelain
import com.chateaucombo.deck.model.Villageois
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParPaireVillageoisChatelain")
data class PointsParPaireVillageoisChatelain(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val cartes = context.joueurActuel.tableau.cartesPositionees
        val nbVillageois = cartes.count { it.carte is Villageois }
        val nbChatelains = cartes.count { it.carte is Chatelain }
        return minOf(nbVillageois, nbChatelains) * points
    }
}
