package com.chateaucombo.strategie

import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.Deck
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.Position

interface Strategie {
    val nom: String
    fun choisitActionCle(joueur: Joueur, decks: List<Deck>): ActionCle
    fun choisitUnDeplacement(joueur: Joueur): DirectionDeplacement
    fun choisitUneCarte(cartesAchetables: List<Carte>, cartesDisponibles: List<Carte>): Carte
    fun choisitUnePosition(positionsAutorisees: List<Position>): Position
}
