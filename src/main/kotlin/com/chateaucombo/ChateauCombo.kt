package com.chateaucombo

import com.chateaucombo.deck.model.Carte
import com.chateaucombo.deck.model.Deck
import com.chateaucombo.deck.repository.DeckRepository
import com.chateaucombo.effet.model.EffetContext
import com.chateaucombo.effet.model.EffetSeparateur.ET
import com.chateaucombo.effet.model.EffetSeparateur.OU
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.joueur.repository.JoueurRepository
import com.chateaucombo.tableau.model.Position
import com.chateaucombo.tableau.model.Position.*
import com.chateaucombo.tableau.model.Tableau
import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Path

class ChateauCombo(
    private val joueurRepository: JoueurRepository,
    private val deckRepository: DeckRepository,
) {
    private val logger = KotlinLogging.logger { }

    fun play(joueurs: List<Joueur>, pathCartes: Path) {
        logger.info { "Début de la partie" }
        val decks = setupLesDecks(pathCartes)
        decks.logCartesDisponibles()
        for (i in 1..9) {
            logger.info { "\n------------ TOUR $i ------------\n" }
            joueurs.forEach { joueur ->
                logger.debug { "Joueur ${joueur.id} : ${joueur.or} or, ${joueur.cle} clés" }
                joueur.utiliseUneCle(decks)

                val currentDeck = decks.first { it.estLeDeckActuel }
                val carte = joueur.placeUneCarte(currentDeck)
                carte.appliqueLesEffets(joueur, joueurs, decks)
                currentDeck.remplitLesCartesDisponibles()

                decks.logCartesDisponibles()
            }
            logger.info { "\n------------ FIN DU TOUR $i ------------\n" }
            joueurs.forEach { joueur -> logger.info { "Joueur ${joueur.id} : ${joueur.or} or, ${joueur.cle} clés" } }
            logLesTableaux(joueurs)
        }
        logger.info { "\n------------ FIN DE LA PARTIE ------------\n" }
    }

    private fun setupLesDecks(path: Path): List<Deck> {
        val (deckChatelains, deckVillageois) = deckRepository.creeDeuxDecksChatelainsEtVillageoisDepuis(path)
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

    private fun Joueur.placeUneCarte(currentDeck: Deck): Carte {
        val carte = this.choisitUneCarte(currentDeck)
        val position = this.choisitUnePosition()
        this.placeUneCarte(carte, position)
        logger.info { "Le joueur ${this.id} a placé la carte ${carte.nom} à la position ${position.name}" }
        return carte
    }

    private fun Joueur.choisitUneCarte(deckChatelains: Deck) =
        joueurRepository.choisitUneCarte(this, deckChatelains)

    private fun Joueur.choisitUnePosition() = joueurRepository.choisitUnePosition(this)

    private fun Joueur.placeUneCarte(carte: Carte, position: Position) {
        val cartePositionee = joueurRepository.placeUneCarte(this, carte, position)
        if (!cartePositionee) error("Problème lors du placement de la carte $carte à la position $position pour le joueur ${this.id} avec le tableau ${this.tableau}")
    }

    private fun Carte.appliqueLesEffets(joueur: Joueur, joueurs: List<Joueur>, decks: List<Deck>) {
        val context = EffetContext(
            joueurActuel = joueur,
            joueurs = joueurs,
            carte = this,
            decks = decks
        )
        when (this.effets.separateur) {
            ET, null -> this.effets.effets.forEach { it.apply(context) }
            OU -> this.effets.effets.random().apply(context)
        }

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
            logger.info { "\nJoueur ${joueur.id} : \n$tableau" }
        }
    }

    private fun Tableau.carteALaPosition(position: Position): String =
        this.cartesPositionees.firstOrNull { it.position == position }?.carte?.nom ?: "   ____   "

    private fun List<Deck>.logCartesDisponibles() {
        logger.debug {
            "\nCartes disponibles : \n" +
                    "${
                        this.joinToString("\n") { deck ->
                            deck.cartesDisponibles.joinToString(
                                prefix = "Deck ${deck.nom} : ",
                                separator = ", "
                            ) { it.nom }
                        }
                    }\n"
        }
    }

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
