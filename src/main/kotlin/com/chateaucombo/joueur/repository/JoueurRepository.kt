package com.chateaucombo.joueur.repository

import com.chateaucombo.deck.model.Carte
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.repository.TableauRepository

class JoueurRepository(
    private val tableauRepository: TableauRepository
) {
    fun prendUneCarte(joueur: Joueur, carte: Carte, position: Position): Boolean =
        tableauRepository.ajouteCarte(tableau = joueur.tableau, carte = carte, position = position)

    fun deplaceAGauche(joueur: Joueur) = tableauRepository.deplaceAGauche(joueur.tableau)

    fun deplaceADroite(joueur: Joueur) = tableauRepository.deplaceADroite(joueur.tableau)

    fun deplaceEnHaut(joueur: Joueur) = tableauRepository.deplaceEnHaut(joueur.tableau)

    fun deplaceEnBas(joueur: Joueur) = tableauRepository.deplaceEnBas(joueur.tableau)
}