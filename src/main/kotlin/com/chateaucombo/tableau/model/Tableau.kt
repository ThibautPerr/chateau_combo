package com.chateaucombo.tableau.model

data class Tableau(
    val cartesPositionees: MutableList<CartePositionee> = mutableListOf()
) {
    fun carteAvecPosition(position: Position): CartePositionee? =
        cartesPositionees.firstOrNull { it.position == position }

    fun pasDeCarteAGauche() =
        this.cartesPositionees.none { it.position.positionHorizontale == PositionHorizontale.GAUCHE }

    fun pasDeCarteADroite() =
        this.cartesPositionees.none { it.position.positionHorizontale == PositionHorizontale.DROITE }

    fun pasDeCarteEnHaut() =
        this.cartesPositionees.none { it.position.positionVerticale == PositionVerticale.HAUT }

    fun pasDeCarteEnBas() =
        this.cartesPositionees.none { it.position.positionVerticale == PositionVerticale.BAS }
}
