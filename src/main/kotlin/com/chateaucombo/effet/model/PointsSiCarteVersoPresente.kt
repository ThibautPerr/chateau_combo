package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.model.CarteVerso

@JsonTypeName("PointsSiCarteVersoPresente")
data class PointsSiCarteVersoPresente(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val aUneCarteVerso = context.joueurActuel.tableau.cartesPositionees
            .any { it.carte is CarteVerso }
        return if (aUneCarteVerso) points else 0
    }
}
