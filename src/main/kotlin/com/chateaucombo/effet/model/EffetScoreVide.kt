package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("EffetScoreVide")
object EffetScoreVide : EffetScore {
    override fun score(context: ScoreContext): Int = 0
}
