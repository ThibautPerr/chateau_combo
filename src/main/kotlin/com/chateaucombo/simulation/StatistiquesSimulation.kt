package com.chateaucombo.simulation

data class StatistiquesSimulation(
    val nbParties: Int,
    val nbJoueurs: Int,
    val strategies: List<String>,
    val global: StatistiquesPoints,
    val parJoueur: List<StatistiquesJoueur>,
) {

    data class StatistiquesPoints(
        val moyenne: Double,
        val premierQuartile: Double,
        val mediane: Double,
        val troisiemeQuartile: Double,
    )

    data class StatistiquesJoueur(
        val nomStrategie: String,
        val moyenne: Double,
        val premierQuartile: Double,
        val mediane: Double,
        val troisiemeQuartile: Double,
        val scoreCarteParTour: List<Double>,
    )
}

data class StatistiquesParStrategie(
    val nomStrategie: String,
    val scoreJoueur: StatistiquesSimulation.StatistiquesPoints,
    val scoreCarte: StatistiquesSimulation.StatistiquesPoints,
)

data class StatistiquesCarte(
    val nomCarte: String,
    val effets: List<String>,
    val effetScore: String,
    val scoreJoueur: StatistiquesSimulation.StatistiquesPoints,
    val scoreCarte: StatistiquesSimulation.StatistiquesPoints,
    val parStrategie: List<StatistiquesParStrategie>,
)

data class StatistiquesEffet(
    val effet: String,
    val cartes: List<String>,
    val scoreJoueur: StatistiquesSimulation.StatistiquesPoints,
    val scoreCarte: StatistiquesSimulation.StatistiquesPoints,
    val parStrategie: List<StatistiquesParStrategie>,
)

data class ResultatSimulation(
    val joueurs: StatistiquesSimulation,
    val cartes: List<StatistiquesCarte>,
    val parEffet: List<StatistiquesEffet>,
    val parEffetScore: List<StatistiquesEffet>,
)
