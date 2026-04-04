package com.chateaucombo.deck.model

import com.chateaucombo.effet.BourseScore
import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.EffetScoreVide
import com.chateaucombo.effet.Effets

data class Chatelain(
    override val cout: Int,
    override val nom: String,
    override val blasons: List<Blason>,
    override val effets: Effets,
    override val effetScore: EffetScore = EffetScoreVide,
    override val bourse: BourseScore? = null,
) : Carte()