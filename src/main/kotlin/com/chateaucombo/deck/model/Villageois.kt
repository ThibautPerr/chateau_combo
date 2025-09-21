package com.chateaucombo.deck.model

data class Villageois (
    override val cout: Int,
    override val nom: String,
    override val blasons: List<Blason>,
): Carte()