package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = EffetScoreVide::class, name = "EffetScoreVide"),
    JsonSubTypes.Type(value = AjoutePoints::class, name = "AjoutePoints"),
    JsonSubTypes.Type(value = PointsParOrDepose::class, name = "PointsParOrDepose"),
    JsonSubTypes.Type(value = PointsSiRangSuperieur::class, name = "PointsSiRangSuperieur"),
)
interface EffetScore {
    fun score(context: ScoreContext): Int
}
