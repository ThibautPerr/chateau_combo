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

        val changerDeck = doitChangerDeck(meilleureActuelle, meilleureAutreDeck)
        memoriseMeilleurCoup(if (changerDeck) meilleureAutreDeck else meilleureActuelle)

        return if (changerDeck) ActionCle.CHANGE_DECK else ActionCle.RIEN
    }

    private fun evaluerMeilleurCoupDeck(joueur: Joueur, deck: Deck, penaliteCle: Int): CoupEvalue? {
        val positions = positionsAutorisees(joueur)
        val reductionVillageois = joueur.reductionCoutVillageois()
        val reductionChatelain = joueur.reductionCoutChatelain()
        val scoreBase = scoreTotalTheorique(joueur)
        return deck.cartesDisponibles
            .filter { joueur.or >= it.coutEffectif(reductionVillageois, reductionChatelain) }
            .flatMap { carte ->
                val coutEff = carte.coutEffectif(reductionVillageois, reductionChatelain)
                positions.map { pos -> evaluerCoup(joueur, carte, pos, penaliteCle, scoreBase, coutEff) }
            }
            .maxByOrNull { it.score }
    }

    private fun positionsAutorisees(joueur: Joueur): List<Position> =
        with(joueur.tableau) {
            if (cartesPositionees.isEmpty()) listOf(Position.MILIEUMILIEU)
            else cartesPositionees
                .flatMap { it.position.positionsAdjacentes() }
                .distinct()
                .filter { pos -> cartesPositionees.none { it.position == pos } }
        }

    private fun Joueur.reductionCoutVillageois(): Int =
        tableau.cartesPositionees
            .flatMap { it.carte.effets.effetsPassifs }
            .filterIsInstance<ReduceCoutVillageois>()
            .size

    private fun Joueur.reductionCoutChatelain(): Int =
        tableau.cartesPositionees
            .flatMap { it.carte.effets.effetsPassifs }
            .filterIsInstance<ReduceCoutChatelain>()
            .size

    private fun Carte.coutEffectif(reductionVillageois: Int, reductionChatelain: Int): Int =
        when (this) {
            is Villageois -> maxOf(0, cout - reductionVillageois)
            is Chatelain -> maxOf(0, cout - reductionChatelain)
            else -> cout
        }

    private fun evaluerCoup(joueur: Joueur, carte: Carte, pos: Position, penaliteCle: Int, scoreBase: Int, coutEffectif: Int): CoupEvalue {
        val cartePositionee = CartePositionee(carte, pos)
        val tableauSimule = Tableau(
            cartesPositionees = (joueur.tableau.cartesPositionees + cartePositionee).toMutableList()
        )
        val joueurSimule = joueur.copy(tableau = tableauSimule)
        val gainTheorique = scoreTotalTheorique(joueurSimule) - scoreBase
        val valeurEffetsPlacement = EvaluateurHeuristique.estimerValeurEffetsPlacement(carte, joueurSimule)
        val coutOpportunite = EvaluateurHeuristique.coutOpportuniteOr(joueur, coutEffectif)
        return CoupEvalue(carte, pos, gainTheorique + valeurEffetsPlacement - coutOpportunite - penaliteCle)
    }

    // Score théorique total du tableau : effets de score + valeur maximale des bourses (taille * 2)
    private fun scoreTotalTheorique(joueur: Joueur): Int =
        joueur.tableau.cartesPositionees.sumOf { cp ->
            val context = EffetScoreContext(joueur, listOf(joueur), cp)
            cp.carte.effetScore.score(context) + (cp.carte.bourse?.taille?.times(2) ?: 0)
        }

    private fun doitChangerDeck(meilleureActuelle: CoupEvalue?, meilleureAutreDeck: CoupEvalue?): Boolean =
        meilleureAutreDeck != null &&
                (meilleureActuelle == null || meilleureAutreDeck.score > meilleureActuelle.score)

    private fun memoriseMeilleurCoup(meilleur: CoupEvalue?) {
        meilleureCartePrevu = meilleur?.carte
        meilleurePositionPrevue = meilleur?.position
    }

    override fun choisitUnDeplacement(joueur: Joueur): DirectionDeplacement = DirectionDeplacement.AUCUN

    override fun choisitUneCarte(cartesAchetables: List<Carte>, cartesDisponibles: List<Carte>): Carte {
        val carte = meilleureCartePrevu?.takeIf { it in cartesAchetables }
        meilleureCartePrevu = null
        return when {
            carte != null -> carte
            cartesAchetables.isNotEmpty() -> cartesAchetables.random()
            else -> carteVersoAleatoire(cartesDisponibles)
        }
    }

    private fun carteVersoAleatoire(cartesDisponibles: List<Carte>): CarteVerso {
        val carteOriginale = cartesDisponibles.random()
        return CarteVerso(nom = "Carte Verso (${carteOriginale.nom})", carteOriginale = carteOriginale)
    }

    override fun choisitUnePosition(positionsAutorisees: List<Position>): Position {
        val pos = meilleurePositionPrevue?.takeIf { it in positionsAutorisees }
        meilleurePositionPrevue = null
        return pos ?: positionsAutorisees.random()
    }
}
