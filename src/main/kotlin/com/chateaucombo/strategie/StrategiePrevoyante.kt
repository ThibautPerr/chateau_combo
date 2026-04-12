package com.chateaucombo.strategie

import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.CarteVerso
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.Deck
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.EffetScoreContext
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutChatelain
import com.chateaucombo.deck.carte.effet.effetplacement.ReduceCoutVillageois
import com.chateaucombo.deck.carte.effet.effetpoint.PointsParOrDepose
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position
import com.chateaucombo.tableau.Tableau

class StrategiePrevoyante : Strategie {

    override val nom = "Prévoyante"

    private data class CoupEvalue(val carte: Carte, val position: Position, val score: Int)

    private var meilleureCartePrevu: Carte? = null
    private var meilleurePositionPrevue: Position? = null
    private var meilleureDirectionPrevue: DirectionDeplacement? = null

    override fun choisitActionCle(joueur: Joueur, decks: List<Deck>): ActionCle {
        val deckActuel = decks.first { it.estLeDeckActuel }
        val autreDeck = decks.first { !it.estLeDeckActuel }

        val (meilleureActuelle, directionActuelle) = evaluerMeilleurCoupToutesDirections(joueur, deckActuel, penaliteCle = 0)
        val (meilleureAutre, directionAutre) = evaluerMeilleurCoupToutesDirections(joueur, autreDeck, penaliteCle = 1)

        val changerDeck = doitChangerDeck(meilleureActuelle, meilleureAutre)
        val (meilleur, direction) = if (changerDeck) Pair(meilleureAutre, directionAutre) else Pair(meilleureActuelle, directionActuelle)
        memoriseMeilleurCoup(meilleur, direction)

        return if (changerDeck) ActionCle.CHANGE_DECK else ActionCle.RIEN
    }

    // scoreOriginal est calculé une fois sur le tableau avant déplacement et sert de référence commune
    // pour toutes les directions, capturant ainsi le bénéfice du déplacement sur les cartes existantes
    private fun evaluerMeilleurCoupToutesDirections(joueur: Joueur, deck: Deck, penaliteCle: Int): Pair<CoupEvalue?, DirectionDeplacement> {
        val scoreOriginal = scoreTotalTheorique(joueur)
        return DirectionDeplacement.entries
            .mapNotNull { direction ->
                val tableauDeplace = simulerDeplacement(joueur.tableau, direction) ?: return@mapNotNull null
                val joueurDeplace = joueur.copy(tableau = tableauDeplace)
                val meilleur = evaluerMeilleurCoupDeck(joueurDeplace, deck, penaliteCle, scoreOriginal)
                meilleur?.let { Pair(it, direction) }
            }
            .maxByOrNull { (coup, _) -> coup.score }
            ?: Pair(null, DirectionDeplacement.AUCUN)
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

    private fun evaluerMeilleurCoupDeck(joueur: Joueur, deck: Deck, penaliteCle: Int, scoreBase: Int): CoupEvalue? {
        val positions = positionsAutorisees(joueur)
        val reductionVillageois = joueur.reductionCoutVillageois()
        val reductionChatelain = joueur.reductionCoutChatelain()
        val orDisponible = orDisponiblePourAchat(joueur)
        return deck.cartesDisponibles
            .filter { orDisponible >= it.coutEffectif(reductionVillageois, reductionChatelain) }
            .flatMap { carte -> positions.map { pos -> evaluerCoup(joueur, carte, pos, penaliteCle, scoreBase) } }
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

    // Reserve au plus la moitié de l'or courant pour remplir les bourses déjà posées
    private fun orDisponiblePourAchat(joueur: Joueur): Int {
        val capaciteRestanteBourses = joueur.tableau.cartesPositionees
            .mapNotNull { it.carte.bourse }
            .sumOf { bourse -> bourse.taille - bourse.orDepose }
        val reserve = minOf(capaciteRestanteBourses, joueur.or / 2)
        return joueur.or - reserve
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

    private fun evaluerCoup(joueur: Joueur, carte: Carte, pos: Position, penaliteCle: Int, scoreBase: Int): CoupEvalue {
        val cartePositionee = CartePositionee(carte, pos)
        val tableauSimule = Tableau(
            cartesPositionees = (joueur.tableau.cartesPositionees + cartePositionee).toMutableList()
        )
        val joueurSimule = joueur.copy(tableau = tableauSimule)
        val gain = scoreTotalTheorique(joueurSimule) - scoreBase
        return CoupEvalue(carte, pos, gain - penaliteCle)
    }

    // Score théorique total : effets de score (PointsParOrDepose supposé rempli) + valeur maximale des bourses (taille * 2)
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

    private fun doitChangerDeck(meilleureActuelle: CoupEvalue?, meilleureAutreDeck: CoupEvalue?): Boolean =
        meilleureAutreDeck != null &&
                (meilleureActuelle == null || meilleureAutreDeck.score > meilleureActuelle.score)

    private fun memoriseMeilleurCoup(meilleur: CoupEvalue?, direction: DirectionDeplacement) {
        meilleureCartePrevu = meilleur?.carte
        meilleurePositionPrevue = meilleur?.position
        meilleureDirectionPrevue = direction
    }

    override fun choisitUnDeplacement(joueur: Joueur): DirectionDeplacement {
        // Utilise la direction pré-calculée par choisitActionCle si disponible,
        // sinon évalue la meilleure direction sur la base du tableau courant
        val direction = meilleureDirectionPrevue ?: evaluerMeilleurDeplacementActuel(joueur)
        meilleureDirectionPrevue = null
        return direction
    }

    // Évalue la meilleure direction sans nouvelles cartes (utilisé quand cle == 0)
    private fun evaluerMeilleurDeplacementActuel(joueur: Joueur): DirectionDeplacement =
        DirectionDeplacement.entries
            .mapNotNull { direction ->
                val tableauDeplace = simulerDeplacement(joueur.tableau, direction) ?: return@mapNotNull null
                val joueurDeplace = joueur.copy(tableau = tableauDeplace)
                Pair(direction, scoreTotalTheorique(joueurDeplace))
            }
            .maxByOrNull { (_, score) -> score }
            ?.first
            ?: DirectionDeplacement.AUCUN

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
