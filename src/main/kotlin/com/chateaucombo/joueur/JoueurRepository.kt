package com.chateaucombo.joueur

import com.chateaucombo.ReglesDuJeu
import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.Deck
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.DeckRepository
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutChatelain
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutVillageois
import com.chateaucombo.tableau.Position
import com.chateaucombo.tableau.TableauRepository

class JoueurRepository(
    private val tableauRepository: TableauRepository,
    private val deckRepository: DeckRepository
) {
    fun choisitUneCarte(joueur: Joueur, deck: Deck): Carte {
        val cartesDisponibles = deck.cartesDisponibles
        val reductionCoutVillageois = joueur.reductionCoutVillageois()
        val reductionCoutChatelain = joueur.reductionCoutChatelain()
        val cartesAchetables =
            cartesDisponibles.filter { carte -> joueur.or >= carte.coutEffectif(reductionCoutVillageois, reductionCoutChatelain) }
        val carteChoisie = joueur.strategie.choisitUneCarte(cartesAchetables, cartesDisponibles)
        joueur.metAJourOr(carteChoisie, reductionCoutVillageois, reductionCoutChatelain)
        joueur.metAJourCle(carteChoisie)
        deck.retireLaCarte(carteChoisie)
        return carteChoisie
    }

    private fun Joueur.reductionCoutVillageois(): Int =
        this.tableau.cartesPositionees
            .flatMap { it.carte.effets.effetsPassifs }
            .filterIsInstance<ReduceCoutVillageois>()
            .size

    private fun Joueur.reductionCoutChatelain(): Int =
        this.tableau.cartesPositionees
            .flatMap { it.carte.effets.effetsPassifs }
            .filterIsInstance<ReduceCoutChatelain>()
            .size

    private fun Carte.coutEffectif(reductionVillageois: Int, reductionChatelain: Int): Int =
        when (this) {
            is Villageois -> maxOf(0, this.cout - reductionVillageois)
            is Chatelain -> maxOf(0, this.cout - reductionChatelain)
            else -> this.cout
        }

    private fun Deck.retireLaCarte(carteChoisie: Carte) {
        when {
            carteChoisie is CarteVerso -> this.cartesDisponibles.remove(carteChoisie.carteOriginale)
            else -> this.cartesDisponibles.remove(carteChoisie)
        }
    }

    private fun Joueur.metAJourOr(carteChoisie: Carte, reductionCoutVillageois: Int, reductionCoutChatelain: Int) {
        if (carteChoisie is CarteVerso) this.or += ReglesDuJeu.OR_CARTE_VERSO
        else this.or = maxOf(0, this.or - carteChoisie.coutEffectif(reductionCoutVillageois, reductionCoutChatelain))
    }

    private fun Joueur.metAJourCle(carteChoisie: Carte) {
        if (carteChoisie is CarteVerso) {
            this.cle += ReglesDuJeu.CLES_CARTE_VERSO
        }
    }

    fun placeUneCarte(joueur: Joueur, carte: Carte, position: Position, tour: Int = 0): Boolean =
        tableauRepository.ajouteCarte(tableau = joueur.tableau, carte = carte, position = position, tour = tour)

    fun deplaceAGauche(joueur: Joueur) = tableauRepository.deplaceAGauche(joueur.tableau)

    fun deplaceADroite(joueur: Joueur) = tableauRepository.deplaceADroite(joueur.tableau)

    fun deplaceEnHaut(joueur: Joueur) = tableauRepository.deplaceEnHaut(joueur.tableau)

    fun deplaceEnBas(joueur: Joueur) = tableauRepository.deplaceEnBas(joueur.tableau)

    fun choisitUnePosition(joueur: Joueur): Position =
        joueur.strategie.choisitUnePosition(tableauRepository.positionsAutorisees(joueur.tableau))

    fun rafraichitLeDeck(joueur: Joueur, deck: Deck) {
        deckRepository.rafraichitLeDeck(deck)
        joueur.cle = maxOf(0, joueur.cle - 1)
    }

    fun changeLeDeckActuel(joueur: Joueur, deckActuel: Deck, prochainDeckActuel: Deck) {
        deckActuel.estLeDeckActuel = false
        prochainDeckActuel.estLeDeckActuel = true
        joueur.cle = maxOf(0, joueur.cle - 1)
    }

}