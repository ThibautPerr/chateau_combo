package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.model.PositionVerticale

@JsonTypeName("PointsSiRangInferieur")
data class PointsSiRangInferieur(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        if (context.cartePositionee.position.positionVerticale == PositionVerticale.BAS) points else 0
}
