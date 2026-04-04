package com.chateaucombo.effet.effetpoint

import com.chateaucombo.deck.model.Chatelain
import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext
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
