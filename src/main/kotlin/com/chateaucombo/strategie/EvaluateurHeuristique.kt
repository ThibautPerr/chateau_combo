package com.chateaucombo.strategie

import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.Effet
import com.chateaucombo.deck.carte.effet.EffetSeparateur
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCle
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCleParBlasonAbsent
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCleParBlasonDistinct
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCleParCarteAvecNbBlason
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCleParCarteBourse
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCleParChatelain
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCleParVillageois
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteClePourChaqueBlason
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteClePourTousLesAdversaires
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteClePourTousLesJoueurs
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrDansBourses
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrParBlasonDistinct
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrParCarteAvecLeCout
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrParCartePositionee
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrParChatelain
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrParEmplacementVide
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrParVillageois
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrPourChaqueBlason
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrPourTousLesAdversaires
import com.chateaucombo.deck.carte.effet.effetplacement.RemplitBourses
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.Position

/**
 * Heuristiques partagées pour l'évaluation des coups par les stratégies.
 *
 * - Estime la valeur en points des effets de placement (or / clés produits → points)
 * - Calcule le coût d'opportunité de l'or dépensé (or qui n'ira pas remplir les bourses)
 *
 * Les effets dépendant des voisins ou des decks (AjouteOr/CleDansTableauVoisin,
 * AjouteOr/CleEnDefaussant...) sont valorisés à 0 car l'incertitude est trop grande.
 */
object EvaluateurHeuristique {

    // Un or déposé dans une bourse vaut 2 pts au scoring final
    private const val VALEUR_OR_EN_BOURSE = 2.0
    // Un or dans la main du joueur ne vaut presque rien s'il n'a pas de bourse à remplir
    private const val VALEUR_OR_HORS_BOURSE = 0.3
    // Une clé vaut 1 pt au scoring final
    private const val VALEUR_CLE = 1.0

    /**
     * Estime la valeur en points des effets de placement de la carte après son placement.
     * Agrège les effets selon le séparateur : ET = somme, OU = moyenne (choix aléatoire).
     */
    fun estimerValeurEffetsPlacement(carte: Carte, joueurApresPlacement: Joueur): Int {
        val effets = carte.effets.effets
        if (effets.isEmpty()) return 0
        val valeurs = effets.map { estimerValeurEffet(it, joueurApresPlacement) }
        return when (carte.effets.separateur) {
            EffetSeparateur.ET -> valeurs.sum().toInt()
            EffetSeparateur.OU -> (valeurs.sum() / valeurs.size).toInt()
        }
    }

    /**
     * Coût d'opportunité de l'or dépensé pour acheter une carte : l'or aurait pu
     * servir à remplir les bourses à raison de 2 pts par or. N'est pénalisé que
     * l'or qui manquera réellement pour combler les bourses.
     */
    fun coutOpportuniteOr(joueur: Joueur, coutEffectif: Int): Int {
        if (coutEffectif <= 0) return 0
        val capaciteRestante = capaciteRestanteBourses(joueur)
        if (capaciteRestante <= 0) return 0
        val orApresAchat = joueur.or - coutEffectif
        val orManquantPourRemplir = maxOf(0, capaciteRestante - orApresAchat)
        val orPerduPourBourse = minOf(coutEffectif, orManquantPourRemplir)
        return (orPerduPourBourse * VALEUR_OR_EN_BOURSE).toInt()
    }

    private fun estimerValeurEffet(effet: Effet, joueurSimule: Joueur): Double {
        val cartes = joueurSimule.tableau.cartesPositionees
        val blasons = cartes.flatMap { it.carte.blasons }
        val valeurOr = valeurOrAttendue(joueurSimule)

        return when (effet) {
            // Or à la main du joueur : valeur selon la disponibilité de bourses à remplir
            is AjouteOrParVillageois -> cartes.count { it.carte is Villageois } * valeurOr
            is AjouteOrParChatelain -> cartes.count { it.carte is Chatelain } * valeurOr
            is AjouteOrParEmplacementVide -> (Position.entries.size - cartes.size) * valeurOr
            is AjouteOrParCartePositionee -> cartes.size * valeurOr
            is AjouteOrParBlasonDistinct -> blasons.distinct().size * valeurOr
            is AjouteOrPourChaqueBlason -> blasons.count { it == effet.blason } * effet.orParBlason * valeurOr
            is AjouteOrParCarteAvecLeCout -> cartes.count { it.carte.cout == effet.cout } * effet.orParCarte * valeurOr

            // Or directement dans les bourses : toujours 2 pts par or
            is AjouteOrDansBourses -> {
                val depose = cartes.mapNotNull { it.carte.bourse }
                    .filter { it.orDepose < it.taille }
                    .sumOf { minOf(effet.or, it.taille - it.orDepose) }
                depose * VALEUR_OR_EN_BOURSE
            }
            is RemplitBourses -> {
                val remplissage = cartes.mapNotNull { it.carte.bourse }
                    .sortedByDescending { it.taille }
                    .take(effet.nb)
                    .sumOf { maxOf(0, it.taille - it.orDepose) }
                remplissage * VALEUR_OR_EN_BOURSE
            }

            // Clés : 1 pt par clé
            is AjouteCle -> effet.cle * VALEUR_CLE
            is AjouteCleParVillageois -> cartes.count { it.carte is Villageois } * VALEUR_CLE
            is AjouteCleParChatelain -> cartes.count { it.carte is Chatelain } * VALEUR_CLE
            is AjouteCleParBlasonDistinct -> blasons.distinct().size * VALEUR_CLE
            is AjouteCleParBlasonAbsent -> (Blason.entries.size - blasons.distinct().size) * VALEUR_CLE
            is AjouteClePourChaqueBlason -> blasons.count { it == effet.blason } * VALEUR_CLE
            is AjouteCleParCarteBourse -> cartes.count { it.carte.bourse != null } * VALEUR_CLE
            is AjouteCleParCarteAvecNbBlason -> cartes.count { it.carte.blasons.size == effet.nbBlason } * VALEUR_CLE
            is AjouteClePourTousLesJoueurs -> effet.cle * VALEUR_CLE

            // Effets qui donnent des ressources aux adversaires : valeur négative
            is AjouteOrPourTousLesAdversaires -> -effet.or * valeurOr
            is AjouteClePourTousLesAdversaires -> -effet.cle * VALEUR_CLE

            // Effets voisins (dépendent du tableau adverse) et effets de defausse (dépendent du deck) :
            // trop incertains pour être estimés fiablement
            else -> 0.0
        }
    }

    private fun valeurOrAttendue(joueur: Joueur): Double =
        if (capaciteRestanteBourses(joueur) > 0) VALEUR_OR_EN_BOURSE else VALEUR_OR_HORS_BOURSE

    private fun capaciteRestanteBourses(joueur: Joueur): Int =
        joueur.tableau.cartesPositionees
            .mapNotNull { it.carte.bourse }
            .sumOf { maxOf(0, it.taille - it.orDepose) }
}
