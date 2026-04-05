package com.chateaucombo.deck.carte.effet.effetpoint

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext

@JsonTypeName("PointsSiCarteVersoPresente")
data class PointsSiCarteVersoPresente(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int {
        val aUneCarteVerso = context.joueurActuel.tableau.cartesPositionees
            .any { it.carte is CarteVerso }
        return if (aUneCarteVerso) points else 0
    }
}
