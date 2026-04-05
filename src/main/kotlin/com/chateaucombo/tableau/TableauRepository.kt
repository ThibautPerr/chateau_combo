package com.chateaucombo.tableau

import com.chateaucombo.deck.carte.Carte

class TableauRepository {
    fun ajouteCarte(tableau: Tableau, carte: Carte, position: Position): Boolean =
        if (tableau.aucuneCarteDejaPositionnee(position)) {
            tableau.cartesPositionees.add(CartePositionee(carte, position))
            true
        } else false

    private fun Tableau.aucuneCarteDejaPositionnee(position: Position) =
        this.cartesPositionees.none { it.position == position }

    fun deplaceAGauche(tableau: Tableau): Boolean {
        val pasDeCarteAGauche = tableau.pasDeCarteAGauche()
        if (pasDeCarteAGauche) tableau.cartesPositionees.replaceAll { it.deplaceAGauche() }
        return pasDeCarteAGauche
    }

    private fun CartePositionee.deplaceAGauche(): CartePositionee {
        val nouvellePosition = requireNotNull(this.position.positionAGauche()) {
            "Impossible de déplacer à gauche depuis ${this.position}"
        }
        return this.copy(position = nouvellePosition)
    }

    fun deplaceADroite(tableau: Tableau): Boolean {
        val pasDeCarteADroite = tableau.pasDeCarteADroite()
        if (pasDeCarteADroite) tableau.cartesPositionees.replaceAll { it.deplaceADroite() }
        return pasDeCarteADroite
    }

    private fun CartePositionee.deplaceADroite(): CartePositionee {
        val nouvellePosition = requireNotNull(this.position.positionADroite()) {
            "Impossible de déplacer à droite depuis ${this.position}"
        }
        return this.copy(position = nouvellePosition)
    }

    fun deplaceEnHaut(tableau: Tableau): Boolean {
        val pasDeCarteEnHaut = tableau.pasDeCarteEnHaut()
        if (pasDeCarteEnHaut) tableau.cartesPositionees.replaceAll { it.deplaceEnHaut() }
        return pasDeCarteEnHaut
    }

    private fun CartePositionee.deplaceEnHaut(): CartePositionee {
        val nouvellePosition = requireNotNull(this.position.positionEnHaut()) {
            "Impossible de déplacer en haut depuis ${this.position}"
        }
        return this.copy(position = nouvellePosition)
    }

    fun deplaceEnBas(tableau: Tableau): Boolean {
        val pasDeCarteEnBas = tableau.pasDeCarteEnBas()
        if (pasDeCarteEnBas) tableau.cartesPositionees.replaceAll { it.deplaceEnBas() }
        return pasDeCarteEnBas
    }

    private fun CartePositionee.deplaceEnBas(): CartePositionee {
        val nouvellePosition = requireNotNull(this.position.positionEnBas()) {
            "Impossible de déplacer en bas depuis ${this.position}"
        }
        return this.copy(position = nouvellePosition)
    }

    fun positionsAutorisees(tableau: Tableau): List<Position> =
        if (tableau.cartesPositionees.isEmpty()) listOf(Position.MILIEUMILIEU)
        else tableau.trouvePositionAutorisees()

    private fun Tableau.trouvePositionAutorisees() =
        this.cartesPositionees
            .map { it.position.positionsAdjacentes() }
            .flatten()
            .distinct()
            .filter { position -> this.cartesPositionees.none { cartePositionnee -> cartePositionnee.position == position } }

}