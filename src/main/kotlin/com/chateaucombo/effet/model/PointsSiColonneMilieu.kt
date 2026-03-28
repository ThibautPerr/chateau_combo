package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.model.PositionHorizontale

@JsonTypeName("PointsSiColonneMilieu")
data class PointsSiColonneMilieu(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        if (context.cartePositionee.position.positionHorizontale == PositionHorizontale.MILIEU) points else 0
}
