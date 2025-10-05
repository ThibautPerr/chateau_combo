package com.chateaucombo.joueur.repository

import com.chateaucombo.deck.model.Carte
import com.chateaucombo.deck.model.CarteVerso
import com.chateaucombo.deck.model.Deck
import com.chateaucombo.deck.repository.DeckRepository
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.repository.TableauRepository

class JoueurRepository(
    private val tableauRepository: TableauRepository,
    private val deckRepository: DeckRepository
) {
    fun choisitUneCarte(joueur: Joueur, deck: Deck): Carte {
        val cartesDisponibles = deck.cartesDisponibles
        val cartesAchetables = cartesDisponibles.filter { carte -> joueur.or >= carte.cout }
        val carteChoisie = choisitUneCarte(cartesAchetables, cartesDisponibles)
        joueur.metAJourOr(carteChoisie)
        joueur.metAJourCle(carteChoisie)
        deck.retireLaCarte(carteChoisie)
        return carteChoisie
    }

    private fun Deck.retireLaCarte(carteChoisie: Carte) {
        when {
            carteChoisie is CarteVerso -> this.cartesDisponibles.remove(carteChoisie.carteOriginale)
            else -> this.cartesDisponibles.remove(carteChoisie)
        }
    }

    private fun choisitUneCarte(cartesAchetables: List<Carte>, cartesDisponibles: List<Carte>) =
        when (cartesAchetables.isNotEmpty()) {
            true -> cartesAchetables.random()
            false -> {
                val carteOriginale = cartesDisponibles.random()
                CarteVerso(nom = "Carte Verso (${carteOriginale.nom})", carteOriginale = carteOriginale)
            }
        }

    private fun Joueur.metAJourOr(carteChoisie: Carte) {
        when (carteChoisie is CarteVerso) {
            true -> this.or += 6
            false -> this.or -= carteChoisie.cout
        }
    }

    private fun Joueur.metAJourCle(carteChoisie: Carte) {
        if (carteChoisie is CarteVerso) {
            this.cle += 2
        }
    }

    fun placeUneCarte(joueur: Joueur, carte: Carte, position: Position): Boolean =
        tableauRepository.ajouteCarte(tableau = joueur.tableau, carte = carte, position = position)

    fun deplaceAGauche(joueur: Joueur) = tableauRepository.deplaceAGauche(joueur.tableau)

    fun deplaceADroite(joueur: Joueur) = tableauRepository.deplaceADroite(joueur.tableau)

    fun deplaceEnHaut(joueur: Joueur) = tableauRepository.deplaceEnHaut(joueur.tableau)

    fun deplaceEnBas(joueur: Joueur) = tableauRepository.deplaceEnBas(joueur.tableau)

    fun choisitUnePosition(joueur: Joueur): Position =
        tableauRepository.choisitUnePosition(joueur.tableau)

    fun rafraichitLeDeck(joueur: Joueur, deck: Deck) {
        deckRepository.rafraichitLeDeck(deck)
        joueur.cle --
    }

    fun changeLeDeckActuel(joueur: Joueur, deckActuel: Deck, prochainDeckActuel: Deck) {
        deckActuel.estLeDeckActuel = false
        prochainDeckActuel.estLeDeckActuel = true
        joueur.cle --
    }

}