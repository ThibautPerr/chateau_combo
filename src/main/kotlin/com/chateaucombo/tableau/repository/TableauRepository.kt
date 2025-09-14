package com.chateaucombo.tableau.repository

import com.chateaucombo.carte.model.Carte
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.model.Tableau

class TableauRepository {
    fun ajouteCarte(tableau: Tableau, carte: Carte, position: Position): Boolean =
        when (tableau.aucuneCarteDejaPositionnee(position)) {
            true -> {
                tableau.cartesPositionees.add(CartePositionee(carte, position))
                true
            }

            false -> false
        }

    private fun Tableau.aucuneCarteDejaPositionnee(position: Position) =
        this.cartesPositionees.none { it.position == position }
}