package com.chateaucombo

import com.chateaucombo.deck.model.Carte
import com.chateaucombo.deck.model.Deck
import com.chateaucombo.deck.repository.DeckRepository
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.joueur.repository.JoueurRepository
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.model.Position.*
import com.chateaucombo.tableau.model.Tableau
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File

class ChateauCombo(
    private val joueurRepository: JoueurRepository,
    private val deckRepository: DeckRepository,
) {
    private val logger = KotlinLogging.logger { }

    fun play(joueurs: List<Joueur>, fichierCartes: File) {
        logger.info { "Début de la partie" }
        val decks = setupLesDecks(fichierCartes)
        for (i in 1..9) {
            logger.info { "\n------------ TOUR $i ------------\n" }
            joueurs.forEach { joueur ->
                logger.info { "Joueur ${joueur.id} : ${joueur.or} or, ${joueur.cle} clés" }
                joueur.utiliseUneCle(decks)

                val currentDeck = decks.first { it.estLeDeckActuel }
                joueur.placeUneCarte(currentDeck)
                currentDeck.remplitLesCartesDisponibles()

                logger.info {
                    "\nCartes disponibles : \n" +
                            "${
                                decks.joinToString("\n") { deck ->
                                    deck.cartesDisponibles.joinToString(
                                        prefix = "  ",
                                        separator = ", "
                                    ) { it.nom }
                                }
                            }\n"
                }
            }
            logger.info { "\n------------ FIN DU TOUR $i ------------\n" }
            logLesTableaux(joueurs)
        }
    }

    private fun setupLesDecks(fichier: File): List<Deck> {
        val (deckChatelains, deckVillageois) = deckRepository.creeDeuxDecksChatelainsEtVillageoisDepuis(fichier)
        deckChatelains.setup()
        deckVillageois.setup()
        return listOf(deckChatelains, deckVillageois)
    }

    private fun Deck.setup() {
        deckRepository.melange(this)
        this.remplitLesCartesDisponibles()
    }

    private fun Deck.remplitLesCartesDisponibles() {
        deckRepository.remplitLesCartesDisponibles(this)
    }

    private fun Joueur.utiliseUneCle(decks: List<Deck>) {
        if (this.cle > 0) {
            when ((0..2).random()) {
                0 -> Unit
                1 -> this.rafraichitLeDeck(decks)
                2 -> this.changeLeDeckActuel(decks)
            }
        }
    }

    private fun Joueur.rafraichitLeDeck(decks: List<Deck>) {
        logger.info { "Le joueur ${this.id} rafraichit le deck ${decks.deckActuel().nom}" }
        joueurRepository.rafraichitLeDeck(this, decks.deckActuel())
    }

    private fun Joueur.changeLeDeckActuel(decks: List<Deck>) {
        logger.info { "Le joueur ${this.id} change le deck actuel (${decks.deckActuel().nom} -> ${decks.prochainDeckActuel().nom})" }
        joueurRepository.changeLeDeckActuel(this, decks.deckActuel(), decks.prochainDeckActuel())
    }

    private fun List<Deck>.deckActuel() = this.first { it.estLeDeckActuel }

    private fun List<Deck>.prochainDeckActuel() = this.first { it.estLeDeckActuel.not() }

    private fun Joueur.placeUneCarte(currentDeck: Deck) {
        val carte = this.choisitUneCarte(currentDeck)
        val position = this.choisitUnePosition()
        this.placeUneCarte(carte, position)
        logger.info { "Le joueur ${this.id} a placé la carte ${carte.nom} à la position ${position.name}" }
    }

    private fun Joueur.choisitUneCarte(deckChatelains: Deck) =
        joueurRepository.choisitUneCarte(this, deckChatelains)

    private fun Joueur.choisitUnePosition() = joueurRepository.choisitUnePosition(this)

    private fun Joueur.placeUneCarte(carte: Carte, position: Position) {
        val cartePositionee = joueurRepository.placeUneCarte(this, carte, position)
        if (!cartePositionee) error("Problème lors du placement de la carte $carte à la position $position pour le joueur ${this.id} avec le tableau ${this.tableau}")
    }

    private fun logLesTableaux(joueurs: List<Joueur>) {
        joueurs.forEach { joueur ->
            val positionsParLigne = listOf(
                listOf(HAUTGAUCHE, HAUTMILIEU, HAUTDROITE),
                listOf(MILIEUGAUCHE, MILIEUMILIEU, MILIEUDROITE),
                listOf(BASGAUCHE, BASMILIEU, BASDROITE)
            )

            val tableau = positionsParLigne.joinToString("\n") { positions ->
                positions.joinToString(" | ") { position ->
                    joueur.tableau.carteALaPosition(position).center()
                }
            }
            logger.info { "Joueur ${joueur.id} : \n$tableau" }
        }
    }

    private fun Tableau.carteALaPosition(position: Position): String =
        this.cartesPositionees.firstOrNull { it.position == position }?.carte?.nom ?: "   ____   "

    private fun String.center(width: Int = 40): String =
        when (this.length >= width) {
            true -> this
            else -> {
                val totalPadding = width - this.length
                val padStart = totalPadding / 2
                val padEnd = totalPadding - padStart
                " ".repeat(padStart) + this + " ".repeat(padEnd)
            }
        }
}
