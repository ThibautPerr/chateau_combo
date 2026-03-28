package com.chateaucombo.deck.model

import com.chateaucombo.effet.model.BourseScore
import com.chateaucombo.effet.model.EffetScore
import com.chateaucombo.effet.model.EffetScoreVide
import com.chateaucombo.effet.model.Effets

data class Villageois(
    override val cout: Int,
    override val nom: String,
    override val blasons: List<Blason>,
    override val effets: Effets,
    override val effetScore: EffetScore = EffetScoreVide,
    override val bourse: BourseScore? = null,
) : Carte()