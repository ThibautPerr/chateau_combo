package com.chateaucombo.joueur.strategie

import com.chateaucombo.deck.model.Carte
import com.chateaucombo.deck.model.Deck
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.Position

interface Strategie {
    val nom: String
    fun choisitActionCle(joueur: Joueur, decks: List<Deck>): ActionCle
    fun choisitUnDeplacement(joueur: Joueur): DirectionDeplacement
    fun choisitUneCarte(cartesAchetables: List<Carte>, cartesDisponibles: List<Carte>): Carte
    fun choisitUnePosition(positionsAutorisees: List<Position>): Position
}
