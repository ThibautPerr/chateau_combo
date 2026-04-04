package com.chateaucombo.effet.effetpoint

import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.ScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParCle")
class PointsParCle : EffetScore {
    override fun score(context: ScoreContext): Int = context.joueurActuel.cle
}
