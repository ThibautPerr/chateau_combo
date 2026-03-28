package com.chateaucombo.deck.model

import com.chateaucombo.effet.model.BourseScore
import com.chateaucombo.effet.model.EffetScore
import com.chateaucombo.effet.model.Effets
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
    abstract val effets: Effets
    abstract val effetScore: EffetScore
    abstract val bourse: BourseScore?
}