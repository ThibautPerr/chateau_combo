package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.tableau.model.PositionHorizontale

@JsonTypeName("PointsSiColonneDroite")
data class PointsSiColonneDroite(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        if (context.cartePositionee.position.positionHorizontale == PositionHorizontale.DROITE) points else 0
}
