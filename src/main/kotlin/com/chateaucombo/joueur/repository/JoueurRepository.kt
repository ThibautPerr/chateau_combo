package com.chateaucombo.joueur.repository

import com.chateaucombo.carte.model.Carte
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.repository.TableauRepository

class JoueurRepository(
    private val tableauRepository: TableauRepository
) {
    fun prendUneCarte(joueur: Joueur, carte: Carte, position: Position): Boolean =
        tableauRepository.ajouteCarte(tableau = joueur.tableau, carte = carte, position = position)
}