package com.chateaucombo.effet.effetpoint

import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.effetplacement.ReduceCoutChatelain
import com.chateaucombo.effet.effetplacement.ReduceCoutVillageois
import com.chateaucombo.effet.ScoreContext
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParCarteAvecReductionDeCout")
data class PointsParCarteAvecReductionDeCout(val points: Int) : EffetScore {
    override fun score(context: ScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees.count { cartePositionee ->
            cartePositionee.carte.effets.effetsPassifs.any {
                it is ReduceCoutChatelain || it is ReduceCoutVillageois
            }
        } * points
}
