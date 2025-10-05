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
    JsonSubTypes.Type(value = AjouteCleParCarteAvecNbBlason::class, name = "AjouteCleParCarteAvecNbBlason"),
    JsonSubTypes.Type(value = AjouteCleParChatelain::class, name = "AjouteCleParChatelain"),
    JsonSubTypes.Type(value = AjouteCleParVillageois::class, name = "AjouteCleParVillageois"),
    JsonSubTypes.Type(value = AjouteClePourChaqueBlason::class, name = "AjouteClePourChaqueBlason"),
    JsonSubTypes.Type(value = AjouteClePourTousLesAdversaires::class, name = "AjouteClePourTousLesAdversaires"),
    JsonSubTypes.Type(value = AjouteClePourTousLesJoueurs::class, name = "AjouteClePourTousLesJoueurs"),
    JsonSubTypes.Type(value = AjouteOrParCarteAvecLeCout::class, name = "AjouteOrParCarteAvecLeCout"),
    JsonSubTypes.Type(value = AjouteOrParChatelain::class, name = "AjouteOrParChatelain"),
    JsonSubTypes.Type(value = AjouteOrParVillageois::class, name = "AjouteOrParVillageois"),
    JsonSubTypes.Type(value = AjouteOrPourChaqueBlason::class, name = "AjouteOrPourChaqueBlason"),
)
interface Effet {
    fun apply(context: EffetContext)
}