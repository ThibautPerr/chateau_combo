package com.chateaucombo.deck.carte

import com.chateaucombo.deck.carte.effet.BourseScore
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreVide
import com.chateaucombo.deck.carte.effet.Effets


data class Chatelain(
    override val cout: Int,
    override val nom: String,
    override val blasons: List<Blason>,
    override val effets: Effets,
    override val effetScore: EffetScore = EffetScoreVide,
    override val bourse: BourseScore? = null,
) : Carte()