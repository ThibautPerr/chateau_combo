package com.chateaucombo.deck.carte.effet

data class Effets(
    val effets: List<Effet> = emptyList(),
    val effetsPassifs: List<EffetPassif> = emptyList(),
    val separateur: EffetSeparateur = EffetSeparateur.ET,
)