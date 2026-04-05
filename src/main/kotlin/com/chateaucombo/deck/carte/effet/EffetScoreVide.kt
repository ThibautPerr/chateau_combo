package com.chateaucombo.deck.carte.effet

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("EffetScoreVide")
object EffetScoreVide : EffetScore {
    override fun score(context: EffetScoreContext): Int = 0
}
