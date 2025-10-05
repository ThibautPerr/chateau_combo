package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AjouteCle::class, name = "AjouteCle"),
    JsonSubTypes.Type(value = AjouteClePourTousLesAdversaires::class, name = "AjouteClePourTousLesAdversaires"),
    JsonSubTypes.Type(value = AjouteClePourTousLesJoueurs::class, name = "AjouteClePourTousLesJoueurs"),
    JsonSubTypes.Type(value = AjouteOrParChatelain::class, name = "AjouteOrParChatelain"),
    JsonSubTypes.Type(value = AjouteOrPourChaqueBlason::class, name = "AjouteOrPourChaqueBlason"),
    JsonSubTypes.Type(value = AjouteCleParCarteAvecUnSeulBlason::class, name = "AjouteCleParCarteAvecUnSeulBlason"),
)
interface Effet {
    fun apply(context: EffetContext)
}