package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.PositionHorizontale

@JsonTypeName("PointsSiColonneGauche")
data class PointsSiColonneGauche(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int =
        if (context.cartePositionee.position.positionHorizontale == PositionHorizontale.GAUCHE) points else 0
}
