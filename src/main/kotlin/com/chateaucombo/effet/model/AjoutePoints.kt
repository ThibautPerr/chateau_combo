package com.chateaucombo.effet.model

data class AjoutePoints(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int = points
}
