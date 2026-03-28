package com.chateaucombo.effet.model

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
