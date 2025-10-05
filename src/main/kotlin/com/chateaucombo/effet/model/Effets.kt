package com.chateaucombo.effet.model

data class Effets(
    val effets: List<Effet> = emptyList(),
    val separateur: EffetSeparateur? = null
)