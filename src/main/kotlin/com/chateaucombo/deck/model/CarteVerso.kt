package com.chateaucombo.deck.model

import com.chateaucombo.effet.model.BourseScore
import com.chateaucombo.effet.model.EffetScore
import com.chateaucombo.effet.model.EffetScoreVide
import com.chateaucombo.effet.model.Effets

data class CarteVerso(
    override val nom: String = "Carte Verso",
    override val cout: Int = -1,
    override val blasons: List<Blason> = emptyList(),
    override val effets: Effets = Effets(),
    override val effetScore: EffetScore = EffetScoreVide,
    override val bourse: BourseScore? = null,
    val carteOriginale: Carte,
) : Carte()
