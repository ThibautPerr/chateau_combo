package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("AjoutePoints")
data class AjoutePoints(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int = points
}
