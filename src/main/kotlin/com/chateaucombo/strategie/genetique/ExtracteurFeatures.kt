package com.chateaucombo.strategie.genetique

import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutChatelain
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutVillageois
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.strategie.ActionCle
import com.chateaucombo.strategie.EvaluateurHeuristique
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position
import com.chateaucombo.tableau.PositionHorizontale
import com.chateaucombo.tableau.PositionVerticale
import com.chateaucombo.tableau.Tableau

/**
 * Encode un coup candidat (carte + position + action clé + tour) en vecteur de features
 * que [Genome] combine ensuite linéairement. Les features sont normalisés autour de petites
 * magnitudes (≈ 0..1) pour que la même fonction puisse alimenter un réseau de neurones plus tard.
 */
object ExtracteurFeatures {

    const val NB_FEATURES = 15

    fun extrait(
        joueurAvant: Joueur,
        carte: Carte,
        position: Position,
        actionCle: ActionCle,
        tour: Int,
    ): FloatArray {
        val coutEff = coutEffectif(carte, joueurAvant)
        val cartePositionee = CartePositionee(carte, position, tour)
        val joueurApres = joueurAvant.copy(
            tableau = Tableau((joueurAvant.tableau.cartesPositionees + cartePositionee).toMutableList())
        )

        val gainScore = scoreTotalTheorique(joueurApres) - scoreTotalTheorique(joueurAvant)
        val valeurEffets = EvaluateurHeuristique.estimerValeurEffetsPlacement(carte, joueurApres)
        val coutOpportunite = EvaluateurHeuristique.coutOpportuniteOr(joueurAvant, coutEff)

        val blasonsRangeeAvant = joueurAvant.tableau.cartesPositionees
            .filter { it.position.positionVerticale == position.positionVerticale }
            .flatMap { it.carte.blasons }
        val blasonsColonneAvant = joueurAvant.tableau.cartesPositionees
            .filter { it.position.positionHorizontale == position.positionHorizontale }
            .flatMap { it.carte.blasons }
        val blasonsIdentiquesEnRangee = carte.blasons.sumOf { b -> blasonsRangeeAvant.count { it == b } }
        val blasonsIdentiquesEnColonne = carte.blasons.sumOf { b -> blasonsColonneAvant.count { it == b } }

        val tousBlasonsApres = joueurApres.tableau.cartesPositionees.flatMap { it.carte.blasons }

        val estCoin = position.positionVerticale != PositionVerticale.MILIEU &&
            position.positionHorizontale != PositionHorizontale.MILIEU
        val estCentre = position == Position.MILIEUMILIEU
        val estBord = !estCoin && !estCentre

        return floatArrayOf(
            gainScore / 10f,
            valeurEffets / 10f,
            coutOpportunite / 10f,
            (joueurAvant.or - coutEff) / 15f,
            if (carte is Chatelain) 1f else 0f,
            if (carte is Villageois) 1f else 0f,
            if (carte is CarteVerso) 1f else 0f,
            if (estCoin) 1f else 0f,
            if (estBord) 1f else 0f,
            if (estCentre) 1f else 0f,
            blasonsIdentiquesEnRangee.toFloat(),
            blasonsIdentiquesEnColonne.toFloat(),
            tousBlasonsApres.distinct().size / 6f,
            tour / 9f,
            if (actionCle != ActionCle.RIEN) 1f else 0f,
        )
    }

    /**
     * Score théorique total d'un tableau : somme des effets de score (`PointsParOrDepose` est
     * approché en supposant que les bourses seront pleines) + valeur maximale des bourses.
     * Identique au calcul utilisé par les stratégies heuristiques existantes.
     */
    private fun scoreTotalTheorique(joueur: Joueur): Int =
        joueur.tableau.cartesPositionees.sumOf { cp ->
            val context = EffetScoreContext(joueur, listOf(joueur), cp)
            cp.carte.effetScore.score(context) + (cp.carte.bourse?.taille?.times(2) ?: 0)
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
}
