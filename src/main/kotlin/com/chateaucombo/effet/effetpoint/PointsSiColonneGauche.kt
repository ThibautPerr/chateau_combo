package com.chateaucombo.effet.effetpoint

import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.model.PositionHorizontale

@JsonTypeName("PointsSiColonneGauche")
data class PointsSiColonneGauche(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        if (context.cartePositionee.position.positionHorizontale == PositionHorizontale.GAUCHE) points else 0
}
