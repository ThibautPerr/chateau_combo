package com.chateaucombo.effet.effetpoint

import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.model.PositionVerticale

@JsonTypeName("PointsSiRangMilieu")
data class PointsSiRangMilieu(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        if (context.cartePositionee.position.positionVerticale == PositionVerticale.MILIEU) points else 0
}
