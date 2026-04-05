package com.chateaucombo.deck.carte.effet.effetpoint

import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutChatelain
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutVillageois
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("PointsParCarteAvecReductionDeCout")
data class PointsParCarteAvecReductionDeCout(val points: Int) : EffetScore {
    override fun score(context: EffetScoreContext): Int =
        context.joueurActuel.tableau.cartesPositionees.count { cartePositionee ->
            cartePositionee.carte.effets.effetsPassifs.any {
                it is ReduceCoutChatelain || it is ReduceCoutVillageois
            }
        } * points
}
