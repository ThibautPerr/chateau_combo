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

class ChateauCombo(
    private val joueurRepository: JoueurRepository,
    private val deckRepository: DeckRepository,
) {
    private val logger = KotlinLogging.logger { }

    fun play(joueurs: List<Joueur>, deckChatelains: Deck, deckVillageois: Deck) {
        logger.info { "Début de la partie" }
        deckChatelains.setup()
        deckVillageois.setup()
        for (i in 1..9) {
            logger.info { "\n------------ TOUR $i ------------\n" }
            joueurs.forEach { joueur ->
                logger.info { "Joueur ${joueur.id} : ${joueur.or} or, ${joueur.cle} clés" }
                val carte = joueur.choisitUneCarte(deckVillageois)
                val position = joueur.choisitUnePosition()
                joueur.placeUneCarte(carte, position)
                logger.info { "Le joueur ${joueur.id} a placé la carte ${carte.nom} à la position ${position.name}" }
                deckVillageois.remplitLesCartesDisponibles()
                logger.info { "\nCartes disponibles : ${deckVillageois.cartesDisponibles.joinToString(separator = ", ") { it.nom }}\n" }
            }
            logger.info { "\n------------ FIN DU TOUR $i ------------\n" }
            logLesTableaux(joueurs)
        }
    }

    private fun Deck.setup() {
        deckRepository.melange(this)
        this.remplitLesCartesDisponibles()
    }

    private fun Deck.remplitLesCartesDisponibles() {
        deckRepository.remplitLesCartesDisponibles(this)
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
