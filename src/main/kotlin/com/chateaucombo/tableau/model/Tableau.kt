package com.chateaucombo.tableau.model

data class Tableau(
    val cartesPositionees: MutableList<CartePositionee> = mutableListOf()
) {
    fun carteAvecPosition(position: Position): CartePositionee? =
        cartesPositionees.firstOrNull { it.position == position }
}
