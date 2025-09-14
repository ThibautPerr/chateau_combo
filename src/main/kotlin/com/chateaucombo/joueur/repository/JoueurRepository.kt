package com.chateaucombo.joueur.repository

import com.chateaucombo.carte.model.Carte
import com.chateaucombo.joueur.model.Joueur

class JoueurRepository {
    fun prendUneCarte(joueur: Joueur, carte: Carte) {
        joueur.cartes.add(carte)
    }
}