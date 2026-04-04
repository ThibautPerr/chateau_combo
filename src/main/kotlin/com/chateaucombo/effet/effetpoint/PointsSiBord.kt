package com.chateaucombo.effet.effetpoint

import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.model.PositionHorizontale
import com.chateaucombo.tableau.model.PositionVerticale

@JsonTypeName("PointsSiBord")
data class PointsSiBord(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int {
        val position = context.cartePositionee.position
        val estBord = (position.positionVerticale == PositionVerticale.MILIEU) xor
                (position.positionHorizontale == PositionHorizontale.MILIEU)
        return if (estBord) points else 0
    }
}
