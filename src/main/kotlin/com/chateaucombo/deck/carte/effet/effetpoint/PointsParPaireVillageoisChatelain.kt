package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParPaireVillageoisChatelain")
data class PointsParPaireVillageoisChatelain(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val cartes = context.joueurActuel.tableau.cartesPositionees
        val nbVillageois = cartes.count { it.carte is Villageois }
        val nbChatelains = cartes.count { it.carte is Chatelain }
        return minOf(nbVillageois, nbChatelains) * points
    }
}
