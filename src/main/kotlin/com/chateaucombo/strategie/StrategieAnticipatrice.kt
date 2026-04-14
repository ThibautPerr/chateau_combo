package com.chateaucombo.strategie

import com.chateaucombo.deck.Deck
import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutChatelain
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutVillageois
import com.chateaucombo.deck.carte.effet.effetpoint.PointsParOrDepose
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position
import com.chateaucombo.tableau.Tableau

/**
 * Variante de StrategiePrevoyante qui :
 * - valorise davantage les clés (penaliteCleDeckSwap = 3), car une clé permet
 *   potentiellement plusieurs actions futures (swap, rafraîchissement, chaînage)
 * - choisit le deck en anticipant le tour suivant : pour chaque option (rester
 *   ou swapper), on simule le coup de ce tour puis on estime le meilleur coup
 *   réalisable au tour prochain en tenant compte des cartes visibles restantes
 *   des DEUX decks.
 */
class StrategieAnticipatrice : Strategie {

    override val nom = "Anticipatrice"

    private data class CoupEvalue(val carte: Carte, val position: Position, val score: Int)

    private var meilleureCartePrevu: Carte? = null
    private var meilleurePositionPrevue: Position? = null
    private var meilleureDirectionPrevue: DirectionDeplacement? = null

    // Une clé vaut 1 pt en fin de partie, mais conserver une clé permet d'enchaîner
    // des actions (swap, rafraîchissement) aux tours suivants : on la valorise à 2.
    private val penaliteCleDeckSwap = 2

    // Seuil minimum pour qu'un déplacement soit choisi : l'évaluation marginale voit le gain
    // immédiat mais pas la perte de flexibilité future (positions adjacentes disponibles).
    private val seuilDeplacement = 3

    // Facteur de discount appliqué à la valeur du tour suivant : les ressources
    // réelles (or, clés) et la main adverse sont incertaines.
    private val discountProchainTour = 0.5

    override fun choisitActionCle(joueur: Joueur, decks: List<Deck>): ActionCle {
        val deckActuel = decks.first { it.estLeDeckActuel }
        val autreDeck = decks.first { !it.estLeDeckActuel }

        val optionRester = evaluerOptionAvecLookahead(joueur, deckAchat = deckActuel, deckAutre = autreDeck, penaliteCle = 0)
        val optionSwapper = evaluerOptionAvecLookahead(joueur, deckAchat = autreDeck, deckAutre = deckActuel, penaliteCle = penaliteCleDeckSwap)

        val changerDeck = doitChangerDeck(optionRester, optionSwapper)
        val option = if (changerDeck) optionSwapper else optionRester
        memoriseMeilleurCoup(option.coup, option.direction)

        return if (changerDeck) ActionCle.CHANGE_DECK else ActionCle.RIEN
    }

    private data class OptionEvaluee(
        val coup: CoupEvalue?,
        val direction: DirectionDeplacement,
        val valeurTotale: Int,
    )

    /**
     * Évalue une option (rester sur un deck / swapper) en calculant :
     * - le gain du meilleur coup de ce tour
     * - plus une estimation du meilleur coup réalisable au tour suivant, en
     *   considérant les cartes visibles restantes des deux decks (deck d'achat
     *   privé de la carte achetée + deck autre inchangé)
     */
    private fun evaluerOptionAvecLookahead(
        joueur: Joueur,
        deckAchat: Deck,
        deckAutre: Deck,
        penaliteCle: Int,
    ): OptionEvaluee {
        val (coup, direction) = evaluerMeilleurCoupToutesDirections(joueur, deckAchat, penaliteCle)
        if (coup == null) return OptionEvaluee(null, direction, Int.MIN_VALUE)

        val joueurApresCoup = simulerJoueurApresCoup(joueur, coup, direction)
        val gainProchainTour = estimerMeilleurGainProchainTour(
            joueurApresCoup = joueurApresCoup,
            cartesVisiblesRestantes = (deckAchat.cartesDisponibles - coup.carte) + deckAutre.cartesDisponibles,
        )
        val valeurTotale = coup.score + (gainProchainTour * discountProchainTour).toInt()
        return OptionEvaluee(coup, direction, valeurTotale)
    }

    /**
     * Simule l'état du joueur après son coup : déplacement + placement de la
     * carte + déduction du coût effectif. Les effets de placement ne sont pas
     * appliqués (leur valeur est déjà intégrée dans coup.score via l'évaluateur
     * heuristique).
     */
    private fun simulerJoueurApresCoup(joueur: Joueur, coup: CoupEvalue, direction: DirectionDeplacement): Joueur {
        val tableauDeplace = simulerDeplacement(joueur.tableau, direction) ?: joueur.tableau
        val tableauAvecCarte = Tableau(
            cartesPositionees = (tableauDeplace.cartesPositionees + CartePositionee(coup.carte, coup.position)).toMutableList()
        )
        val reductionVillageois = joueur.reductionCoutVillageois()
        val reductionChatelain = joueur.reductionCoutChatelain()
        val coutEffectif = coup.carte.coutEffectif(reductionVillageois, reductionChatelain)
        return joueur.copy(tableau = tableauAvecCarte, or = maxOf(0, joueur.or - coutEffectif))
    }

    /**
     * Estime le meilleur gain possible au tour suivant parmi les cartes visibles
     * restantes (sur les deux decks). Pas de pénalité de clé : on suppose que le
     * joueur choisira librement au tour prochain.
     */
    private fun estimerMeilleurGainProchainTour(joueurApresCoup: Joueur, cartesVisiblesRestantes: List<Carte>): Int {
        if (cartesVisiblesRestantes.isEmpty()) return 0
        val (coup, _) = evaluerMeilleurCoupSurCartes(joueurApresCoup, cartesVisiblesRestantes, penaliteCle = 0)
        return coup?.score ?: 0
    }

    private fun evaluerMeilleurCoupSurCartes(
        joueur: Joueur,
        cartes: List<Carte>,
        penaliteCle: Int,
    ): Pair<CoupEvalue?, DirectionDeplacement> {
        val scoreOriginal = scoreTotalTheorique(joueur)
        val coupsParDirection = DirectionDeplacement.entries
            .mapNotNull { direction ->
                val tableauDeplace = simulerDeplacement(joueur.tableau, direction) ?: return@mapNotNull null
                val joueurDeplace = joueur.copy(tableau = tableauDeplace)
                val meilleur = evaluerMeilleurCoupParmi(joueurDeplace, cartes, penaliteCle, scoreOriginal)
                meilleur?.let { Pair(it, direction) }
            }
        val sansDeplacement = coupsParDirection.firstOrNull { it.second == DirectionDeplacement.AUCUN }
        val meilleurAvecDeplacement = coupsParDirection
            .filter { it.second != DirectionDeplacement.AUCUN }
            .maxByOrNull { it.first.score }

        return when {
            meilleurAvecDeplacement == null && sansDeplacement == null -> Pair(null, DirectionDeplacement.AUCUN)
            meilleurAvecDeplacement == null -> sansDeplacement!!
            sansDeplacement == null -> meilleurAvecDeplacement
            meilleurAvecDeplacement.first.score >= sansDeplacement.first.score + seuilDeplacement -> meilleurAvecDeplacement
            else -> sansDeplacement
        }
    }

    private fun evaluerMeilleurCoupToutesDirections(joueur: Joueur, deck: Deck, penaliteCle: Int): Pair<CoupEvalue?, DirectionDeplacement> =
        evaluerMeilleurCoupSurCartes(joueur, deck.cartesDisponibles, penaliteCle)

    private fun evaluerMeilleurCoupParmi(joueur: Joueur, cartes: List<Carte>, penaliteCle: Int, scoreBase: Int): CoupEvalue? {
        val positions = positionsAutorisees(joueur)
        val reductionVillageois = joueur.reductionCoutVillageois()
        val reductionChatelain = joueur.reductionCoutChatelain()
        return cartes
            .filter { joueur.or >= it.coutEffectif(reductionVillageois, reductionChatelain) }
            .flatMap { carte ->
                val coutEff = carte.coutEffectif(reductionVillageois, reductionChatelain)
                positions.map { pos -> evaluerCoup(joueur, carte, pos, penaliteCle, scoreBase, coutEff) }
            }
            .maxByOrNull { it.score }
    }

    private fun simulerDeplacement(tableau: Tableau, direction: DirectionDeplacement): Tableau? =
        when (direction) {
            DirectionDeplacement.AUCUN -> tableau
            DirectionDeplacement.GAUCHE -> if (tableau.pasDeCarteAGauche())
                Tableau(tableau.cartesPositionees.map { it.copy(position = requireNotNull(it.position.positionAGauche())) }.toMutableList())
            else null
            DirectionDeplacement.DROITE -> if (tableau.pasDeCarteADroite())
                Tableau(tableau.cartesPositionees.map { it.copy(position = requireNotNull(it.position.positionADroite())) }.toMutableList())
            else null
            DirectionDeplacement.HAUT -> if (tableau.pasDeCarteEnHaut())
                Tableau(tableau.cartesPositionees.map { it.copy(position = requireNotNull(it.position.positionEnHaut())) }.toMutableList())
            else null
            DirectionDeplacement.BAS -> if (tableau.pasDeCarteEnBas())
                Tableau(tableau.cartesPositionees.map { it.copy(position = requireNotNull(it.position.positionEnBas())) }.toMutableList())
            else null
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

    private fun scoreTotalTheorique(joueur: Joueur): Int =
        joueur.tableau.cartesPositionees.sumOf { cp -> scoreTheorique(joueur, cp) }

    private fun scoreTheorique(joueur: Joueur, cartePositionee: CartePositionee): Int {
        val context = EffetScoreContext(joueur, listOf(joueur), cartePositionee)
        val effectScore = when (cartePositionee.carte.effetScore) {
            is PointsParOrDepose -> joueur.tableau.cartesPositionees
                .mapNotNull { it.carte.bourse }
                .sumOf { it.taille }
            else -> cartePositionee.carte.effetScore.score(context)
        }
        val bourseScore = cartePositionee.carte.bourse?.taille?.times(2) ?: 0
        return effectScore + bourseScore
    }

    private fun doitChangerDeck(optionRester: OptionEvaluee, optionSwapper: OptionEvaluee): Boolean =
        optionSwapper.coup != null &&
                (optionRester.coup == null || optionSwapper.valeurTotale > optionRester.valeurTotale)

    private fun memoriseMeilleurCoup(meilleur: CoupEvalue?, direction: DirectionDeplacement) {
        meilleureCartePrevu = meilleur?.carte
        meilleurePositionPrevue = meilleur?.position
        meilleureDirectionPrevue = direction
    }

    override fun choisitUnDeplacement(joueur: Joueur): DirectionDeplacement {
        val direction = meilleureDirectionPrevue ?: evaluerMeilleurDeplacementActuel(joueur)
        meilleureDirectionPrevue = null
        return direction
    }

    private fun evaluerMeilleurDeplacementActuel(joueur: Joueur): DirectionDeplacement {
        val scoreSansDeplacement = scoreTotalTheorique(joueur)
        val meilleurAvecDeplacement = DirectionDeplacement.entries
            .filter { it != DirectionDeplacement.AUCUN }
            .mapNotNull { direction ->
                val tableauDeplace = simulerDeplacement(joueur.tableau, direction) ?: return@mapNotNull null
                val joueurDeplace = joueur.copy(tableau = tableauDeplace)
                Pair(direction, scoreTotalTheorique(joueurDeplace))
            }
            .maxByOrNull { (_, score) -> score }
        return when {
            meilleurAvecDeplacement == null -> DirectionDeplacement.AUCUN
            meilleurAvecDeplacement.second >= scoreSansDeplacement + seuilDeplacement -> meilleurAvecDeplacement.first
            else -> DirectionDeplacement.AUCUN
        }
    }

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
