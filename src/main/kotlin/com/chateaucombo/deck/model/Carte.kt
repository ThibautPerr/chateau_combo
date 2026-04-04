package com.chateaucombo.deck.model

import com.chateaucombo.effet.BourseScore
import com.chateaucombo.effet.EffetScore
import com.chateaucombo.effet.Effets
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
sealed class Carte {
    abstract val cout: Int
    abstract val nom: String
    abstract val blasons: List<Blason>
    abstract val effets: com.chateaucombo.effet.Effets
    abstract val effetScore: com.chateaucombo.effet.EffetScore
    abstract val bourse: com.chateaucombo.effet.BourseScore?
}