package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason
import com.chateaucombo.deck.model.Chatelain
import com.chateaucombo.deck.model.Villageois
import com.chateaucombo.effet.model.*

abstract class EffetTestBase {
    protected fun villageois(
        cout: Int = 0,
        blasons: List<Blason> = emptyList(),
        effets: Effets = Effets(),
        effetScore: EffetScore = EffetScoreVide,
        bourse: BourseScore? = null
    ) = Villageois(cout = cout, nom = "carte", blasons = blasons, effets = effets, effetScore = effetScore, bourse = bourse)

    protected fun chatelain(
        effets: Effets = Effets(),
        blasons: List<Blason> = emptyList(),
        effetScore: EffetScore = EffetScoreVide,
    ) = Chatelain(cout = 0, nom = "carte", blasons = blasons, effets = effets, effetScore = effetScore)
}
