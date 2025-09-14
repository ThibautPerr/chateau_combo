package com.chateaucombo.carte.repository

import com.chateaucombo.carte.model.Carte
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

class CarteRepository {

    private val mapper = jacksonObjectMapper()

    fun litLesCartesDepuis(file: File): List<Carte> = mapper.readValue<List<Carte>>(file)
}