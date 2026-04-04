package com.chateaucombo.deck.model

import com.chateaucombo.effet.BourseScore
import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.EffetScoreVide
import com.chateaucombo.effet.Effets

data class Chatelain(
    override val cout: Int,
    override val nom: String,
    override val blasons: List<Blason>,
    override val effets: com.chateaucombo.effet.Effets,
    override val effetScore: com.chateaucombo.effet.EffetScore = _root_ide_package_.com.chateaucombo.effet.EffetScoreVide,
    override val bourse: com.chateaucombo.effet.BourseScore? = null,
) : Carte()