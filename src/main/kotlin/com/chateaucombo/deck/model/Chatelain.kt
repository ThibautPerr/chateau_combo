package com.chateaucombo.deck.model

import com.chateaucombo.effet.model.EffetScore
import com.chateaucombo.effet.model.EffetScoreVide
import com.chateaucombo.effet.model.Effets

data class Chatelain(
    override val cout: Int,
    override val nom: String,
    override val blasons: List<Blason>,
    override val effets: Effets,
    override val effetScore: EffetScore = EffetScoreVide,
) : Carte()