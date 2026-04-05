package com.chateaucombo.strategie

import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.Deck
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.Position

class StrategieAleatoire : Strategie {
    override val nom = "Aléatoire"

    override fun choisitActionCle(joueur: Joueur, decks: List<Deck>): ActionCle =
        when ((0..2).random()) {
            1 -> ActionCle.RAFRAICHIT
            2 -> ActionCle.CHANGE_DECK
            else -> ActionCle.RIEN
        }

    override fun choisitUnDeplacement(joueur: Joueur): DirectionDeplacement = DirectionDeplacement.AUCUN

    override fun choisitUneCarte(cartesAchetables: List<Carte>, cartesDisponibles: List<Carte>): Carte =
        if (cartesAchetables.isNotEmpty()) cartesAchetables.random()
        else {
            val carteOriginale = cartesDisponibles.random()
            CarteVerso(nom = "Carte Verso (${carteOriginale.nom})", carteOriginale = carteOriginale)
        }

    override fun choisitUnePosition(positionsAutorisees: List<Position>): Position =
        positionsAutorisees.random()
}
