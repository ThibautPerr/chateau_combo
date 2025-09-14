package com.chateaucombo.carte.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Chatelain::class, name = "CHATELAIN"),
    JsonSubTypes.Type(value = Villageois::class, name = "VILLAGEOIS")
)
sealed class Carte {
    abstract val cout: Int
    abstract val nom: String
    abstract val blasons: List<Blason>
}