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
    JsonSubTypes.Type(value = AjouteCleParBlasonAbsent::class, name = "AjouteCleParBlasonAbsent"),
    JsonSubTypes.Type(value = AjouteCleParBlasonDansTableauVoisin::class, name = "AjouteCleParBlasonDansTableauVoisin"),
    JsonSubTypes.Type(value = AjouteCleParChatelainDansTableauVoisin::class, name = "AjouteCleParChatelainDansTableauVoisin"),
    JsonSubTypes.Type(value = AjouteCleParBlasonDistinct::class, name = "AjouteCleParBlasonDistinct"),
    JsonSubTypes.Type(value = AjouteCleParCarteAvecNbBlason::class, name = "AjouteCleParCarteAvecNbBlason"),
    JsonSubTypes.Type(value = AjouteCleParChatelain::class, name = "AjouteCleParChatelain"),
    JsonSubTypes.Type(value = AjouteCleParVillageois::class, name = "AjouteCleParVillageois"),
    JsonSubTypes.Type(value = AjouteClePourChaqueBlason::class, name = "AjouteClePourChaqueBlason"),
    JsonSubTypes.Type(value = AjouteClePourTousLesAdversaires::class, name = "AjouteClePourTousLesAdversaires"),
    JsonSubTypes.Type(value = AjouteClePourTousLesJoueurs::class, name = "AjouteClePourTousLesJoueurs"),
    JsonSubTypes.Type(value = AjouteOrParBlasonDistinct::class, name = "AjouteOrParBlasonDistinct"),
    JsonSubTypes.Type(value = AjouteOrParBlasonDansTableauVoisin::class, name = "AjouteOrParBlasonDansTableauVoisin"),
    JsonSubTypes.Type(value = AjouteOrParCarteAvecLeCout::class, name = "AjouteOrParCarteAvecLeCout"),
    JsonSubTypes.Type(value = AjouteOrParCartePositionee::class, name = "AjouteOrParCartePositionee"),
    JsonSubTypes.Type(value = AjouteOrParEmplacementVide::class, name = "AjouteOrParEmplacementVide"),
    JsonSubTypes.Type(value = AjouteOrParChatelain::class, name = "AjouteOrParChatelain"),
    JsonSubTypes.Type(value = AjouteOrParVillageois::class, name = "AjouteOrParVillageois"),
    JsonSubTypes.Type(value = AjouteOrPourChaqueBlason::class, name = "AjouteOrPourChaqueBlason"),
    JsonSubTypes.Type(value = AjouteOrPourTousLesAdversaires::class, name = "AjouteOrPourTousLesAdversaires"),
    JsonSubTypes.Type(value = AjouteOrEnDefaussantUnVillageois::class, name = "AjouteOrEnDefaussantUnVillageois"),
    JsonSubTypes.Type(value = AjouteOrEnDefaussantUnChatelain::class, name = "AjouteOrEnDefaussantUnChatelain"),
)
interface Effet {
    fun apply(context: EffetContext)
}