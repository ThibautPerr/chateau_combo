package com.chateaucombo.deck.model

data class CarteVerso(
    override val nom: String = "Carte Verso",
    override val cout: Int = -1,
    override val blasons: List<Blason> = emptyList(),
    val carteOriginale: Carte
) : Carte()
