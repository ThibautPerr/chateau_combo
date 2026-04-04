package com.chateaucombo.effet.effetpoint

import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("AjoutePoints")
data class AjoutePoints(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int = points
}