package com.chateaucombo.effet

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("EffetScoreVide")
object EffetScoreVide : EffetScore {
    override fun score(context: ScoreContext): Int = 0
}
