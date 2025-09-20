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

    fun deplaceAGauche(tableau: Tableau): Boolean {
        val pasDeCarteAGauche = tableau.pasDeCarteAGauche()
        if (pasDeCarteAGauche) tableau.cartesPositionees.replaceAll { it.deplaceAGauche() }
        return pasDeCarteAGauche
    }

    private fun CartePositionee.deplaceAGauche(): CartePositionee {
        val nouvellePosition = this.position.positionAGauche()
        return this.copy(position = nouvellePosition)
    }

    fun deplaceADroite(tableau: Tableau): Boolean {
        val pasDeCarteADroite = tableau.pasDeCarteADroite()
        if (pasDeCarteADroite) tableau.cartesPositionees.replaceAll { it.deplaceADroite() }
        return pasDeCarteADroite
    }

    private fun CartePositionee.deplaceADroite(): CartePositionee {
        val nouvellePosition = this.position.positionADroite()
        return this.copy(position = nouvellePosition)
    }

    fun deplaceEnHaut(tableau: Tableau): Boolean {
        val pasDeCarteEnHaut = tableau.pasDeCarteEnHaut()
        if (pasDeCarteEnHaut) tableau.cartesPositionees.replaceAll { it.deplaceEnHaut() }
        return pasDeCarteEnHaut
    }

    private fun CartePositionee.deplaceEnHaut(): CartePositionee {
        val nouvellePosition = this.position.positionEnHaut()
        return this.copy(position = nouvellePosition)
    }

    fun deplaceEnBas(tableau: Tableau): Boolean {
        val pasDeCarteEnBas = tableau.pasDeCarteEnBas()
        if (pasDeCarteEnBas) tableau.cartesPositionees.replaceAll { it.deplaceEnBas() }
        return pasDeCarteEnBas
    }

    private fun CartePositionee.deplaceEnBas(): CartePositionee {
        val nouvellePosition = this.position.positionEnBas()
        return this.copy(position = nouvellePosition)
    }
}