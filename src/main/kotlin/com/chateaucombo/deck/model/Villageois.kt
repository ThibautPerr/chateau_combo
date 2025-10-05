package com.chateaucombo.deck.model

import com.chateaucombo.effet.model.Effets

data class Villageois(
    override val cout: Int,
    override val nom: String,
    override val blasons: List<Blason>,
    override val effets: Effets,
) : Carte()