package com.chateaucombo.deck.model

import com.chateaucombo.effet.BourseScore
import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.EffetScoreVide
import com.chateaucombo.effet.Effets

data class CarteVerso(
    override val nom: String = "Carte Verso",
    override val cout: Int = 0,
    override val blasons: List<Blason> = emptyList(),
    override val effets: Effets = Effets(),
    override val effetScore: EffetScore = EffetScoreVide,
    override val bourse: BourseScore? = null,
    val carteOriginale: Carte,
) : Carte()
