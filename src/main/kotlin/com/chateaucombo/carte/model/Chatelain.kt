package com.chateaucombo.carte.model

data class Chatelain(
    override val cout: Int,
    override val nom: String,
    override val blasons: List<Blason>
) : Carte()