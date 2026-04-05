package com.chateaucombo.deck.carte

import com.chateaucombo.deck.carte.effet.BourseScore
import com.chateaucombo.deck.carte.effet.EffetScore
import com.chateaucombo.deck.carte.effet.Effets
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