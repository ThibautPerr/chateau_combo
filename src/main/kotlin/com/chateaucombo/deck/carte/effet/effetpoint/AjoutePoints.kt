package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("AjoutePoints")
data class AjoutePoints(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int = points
}