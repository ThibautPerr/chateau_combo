package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParCle")
class PointsParCle : EffetScore {
    override fun score(context: EffetScoreContext): Int = context.joueurActuel.cle
}
