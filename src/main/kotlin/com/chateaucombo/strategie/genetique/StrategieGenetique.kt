package com.chateaucombo.strategie.genetique

import com.chateaucombo.deck.Deck
import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutChatelain
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutVillageois
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.strategie.ActionCle
import com.chateaucombo.strategie.DirectionDeplacement
import com.chateaucombo.strategie.Strategie
import com.chateaucombo.tableau.Position

/**
 * Stratégie pilotée par un [Genome] de poids appris. Le score de chaque coup
 * candidat (carte × position × action clé) est `genome · features`, et la
 * stratégie choisit l'argmax. Les déplacements ne sont pas évalués pour ce
 * premier jet (toujours [DirectionDeplacement.AUCUN]).
 */
class StrategieGenetique(
    val genome: Genome = Genome.parDefaut(),
    override val nom: String = "Genetique",
) : Strategie {

    private data class CoupEvalue(
        val carte: Carte,
        val position: Position,
        val score: Float,
    )

    private var meilleureCartePrevue: Carte? = null
    private var meilleurePositionPrevue: Position? = null

    override fun choisitActionCle(joueur: Joueur, decks: List<Deck>): ActionCle {
        val tour = joueur.tableau.cartesPositionees.size + 1
        val deckActuel = decks.first { it.estLeDeckActuel }
        val autreDeck = decks.first { !it.estLeDeckActuel }

        val coupRien = meilleurCoupSurDeck(joueur, deckActuel, ActionCle.RIEN, tour)
        val coupChange = if (joueur.cle > 0)
            meilleurCoupSurDeck(joueur, autreDeck, ActionCle.CHANGE_DECK, tour)
        else null

        val (action, meilleur) = when {
            coupChange != null && (coupRien == null || coupChange.score > coupRien.score) ->
                ActionCle.CHANGE_DECK to coupChange
            else -> ActionCle.RIEN to coupRien
        }
        meilleureCartePrevue = meilleur?.carte
        meilleurePositionPrevue = meilleur?.position
        return action
    }

    override fun choisitUnDeplacement(joueur: Joueur): DirectionDeplacement = DirectionDeplacement.AUCUN

    override fun choisitUneCarte(cartesAchetables: List<Carte>, cartesDisponibles: List<Carte>): Carte {
        val cible = meilleureCartePrevue?.takeIf { it in cartesAchetables }
        meilleureCartePrevue = null
        return when {
            cible != null -> cible
            cartesAchetables.isNotEmpty() -> cartesAchetables.random()
            else -> carteVersoAleatoire(cartesDisponibles)
        }
    }

    override fun choisitUnePosition(positionsAutorisees: List<Position>): Position {
        val pos = meilleurePositionPrevue?.takeIf { it in positionsAutorisees }
        meilleurePositionPrevue = null
        return pos ?: positionsAutorisees.random()
    }

    private fun meilleurCoupSurDeck(joueur: Joueur, deck: Deck, actionCle: ActionCle, tour: Int): CoupEvalue? {
        val positions = positionsAutorisees(joueur)
        return deck.cartesDisponibles
            .filter { joueur.or >= coutEffectif(it, joueur) }
            .flatMap { carte -> positions.map { pos -> evaluerCoup(joueur, carte, pos, actionCle, tour) } }
            .maxByOrNull { it.score }
    }

    private fun evaluerCoup(joueur: Joueur, carte: Carte, position: Position, actionCle: ActionCle, tour: Int): CoupEvalue {
        val features = ExtracteurFeatures.extrait(joueur, carte, position, actionCle, tour)
        return CoupEvalue(carte, position, genome.score(features))
    }

    private fun positionsAutorisees(joueur: Joueur): List<Position> =
        with(joueur.tableau) {
            if (cartesPositionees.isEmpty()) listOf(Position.MILIEUMILIEU)
            else cartesPositionees
                .flatMap { it.position.positionsAdjacentes() }
                .distinct()
                .filter { pos -> cartesPositionees.none { it.position == pos } }
        }

    private fun coutEffectif(carte: Carte, joueur: Joueur): Int {
        val passifs = joueur.tableau.cartesPositionees.flatMap { it.carte.effets.effetsPassifs }
        val reductionVillageois = passifs.count { it is ReduceCoutVillageois }
        val reductionChatelain = passifs.count { it is ReduceCoutChatelain }
        return when (carte) {
            is Villageois -> maxOf(0, carte.cout - reductionVillageois)
            is Chatelain -> maxOf(0, carte.cout - reductionChatelain)
            else -> carte.cout
        }
    }

    private fun carteVersoAleatoire(cartesDisponibles: List<Carte>): CarteVerso {
        val carteOriginale = cartesDisponibles.random()
        return CarteVerso(nom = "Carte Verso (${carteOriginale.nom})", carteOriginale = carteOriginale)
    }
}
