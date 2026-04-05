package com.chateaucombo.deck.carte

import com.chateaucombo.deck.carte.effet.BourseScore
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.EffetScoreVide
import com.chateaucombo.deck.carte.effet.Effets


data class CarteVerso(
    override val nom: String = "Carte Verso",
    override val cout: Int = 0,
    override val blasons: List<Blason> = emptyList(),
    override val effets: Effets = Effets(),
    override val effetScore: EffetScore = EffetScoreVide,
    override val bourse: BourseScore? = null,
    val carteOriginale: Carte,
) : Carte()
