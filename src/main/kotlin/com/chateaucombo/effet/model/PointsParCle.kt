package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParCle")
class PointsParCle : EffetScore {
    override fun score(context: ScoreContext): Int = context.joueurActuel.cle
}
