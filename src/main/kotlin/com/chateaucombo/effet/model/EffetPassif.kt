package com.chateaucombo.effet.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ReduceCoutVillageois::class, name = "ReduceCoutVillageois"),
    JsonSubTypes.Type(value = ReduceCoutChatelain::class, name = "ReduceCoutChatelain"),
)
interface EffetPassif
