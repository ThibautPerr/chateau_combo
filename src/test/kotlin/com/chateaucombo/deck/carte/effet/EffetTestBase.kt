package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.carte.Villageois

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
