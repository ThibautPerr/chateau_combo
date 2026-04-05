package com.chateaucombo.strategie

import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.Deck
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutChatelain
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutVillageois
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position
import com.chateaucombo.tableau.Tableau

class StrategieGourmande : Strategie {

    override val nom = "Gourmande"

    private data class CoupEvalue(val carte: Carte, val position: Position, val score: Int)

    private var meilleureCartePrevu: Carte? = null
    private var meilleurePositionPrevue: Position? = null

    override fun choisitActionCle(joueur: Joueur, decks: List<Deck>): ActionCle {
        val deckActuel = decks.first { it.estLeDeckActuel }
        val autreDeck = decks.first { !it.estLeDeckActuel }

        val meilleureActuelle = evaluerMeilleurCoupDeck(joueur, deckActuel, penaliteCle = 0)
        val meilleureAutreDeck = evaluerMeilleurCoupDeck(joueur, autreDeck, penaliteCle = 1)

        val changerDeck = meilleureAutreDeck != null &&
            (meilleureActuelle == null || meilleureAutreDeck.score > meilleureActuelle.score)

        val meilleur = if (changerDeck) meilleureAutreDeck else meilleureActuelle
        meilleureCartePrevu = meilleur?.carte
        meilleurePositionPrevue = meilleur?.position

        return if (changerDeck) ActionCle.CHANGE_DECK else ActionCle.RIEN
    }

    override fun choisitUnDeplacement(joueur: Joueur): DirectionDeplacement = DirectionDeplacement.AUCUN

    override fun choisitUneCarte(cartesAchetables: List<Carte>, cartesDisponibles: List<Carte>): Carte {
        val carte = meilleureCartePrevu?.takeIf { cartesAchetables.contains(it) }
        meilleureCartePrevu = null
        return when {
            carte != null -> carte
            cartesAchetables.isNotEmpty() -> cartesAchetables.random()
            else -> {
                val carteOriginale = cartesDisponibles.random()
                CarteVerso(nom = "Carte Verso (${carteOriginale.nom})", carteOriginale = carteOriginale)
            }
        }
    }

    override fun choisitUnePosition(positionsAutorisees: List<Position>): Position {
        val pos = meilleurePositionPrevue?.takeIf { positionsAutorisees.contains(it) }
        meilleurePositionPrevue = null
        return pos ?: positionsAutorisees.random()
    }

    private fun evaluerMeilleurCoupDeck(joueur: Joueur, deck: Deck, penaliteCle: Int): CoupEvalue? {
        val positions = positionsAutorisees(joueur)
        val reductionVillageois = joueur.reductionCoutVillageois()
        val reductionChatelain = joueur.reductionCoutChatelain()
        return deck.cartesDisponibles
            .filter { joueur.or >= it.coutEffectif(reductionVillageois, reductionChatelain) }
            .flatMap { carte -> positions.map { pos -> evaluerCoup(joueur, carte, pos, penaliteCle) } }
            .maxByOrNull { it.score }
    }

    private fun evaluerCoup(joueur: Joueur, carte: Carte, position: Position, penaliteCle: Int): CoupEvalue {
        val cartePositionee = CartePositionee(carte, position)
        val tableauSimule = Tableau(
            cartesPositionees = (joueur.tableau.cartesPositionees + cartePositionee).toMutableList()
        )
        val joueurSimule = joueur.copy(tableau = tableauSimule)
        val context = EffetScoreContext(joueurSimule, listOf(joueurSimule), cartePositionee)
        return CoupEvalue(carte, position, carte.effetScore.score(context) - penaliteCle)
    }

    private fun positionsAutorisees(joueur: Joueur): List<Position> {
        val tableau = joueur.tableau
        return if (tableau.cartesPositionees.isEmpty()) listOf(Position.MILIEUMILIEU)
        else tableau.cartesPositionees
            .flatMap { it.position.positionsAdjacentes() }
            .distinct()
            .filter { pos -> tableau.cartesPositionees.none { it.position == pos } }
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
}
