package com.chateaucombo.joueur.strategie.genetique

import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.Carte
import com.chateaucombo.deck.carte.Chatelain
import com.chateaucombo.deck.carte.Villageois
import com.chateaucombo.deck.carte.effet.BourseScore
import com.chateaucombo.deck.carte.effet.EffetScoreVide
import com.chateaucombo.deck.carte.effet.Effets
import com.chateaucombo.deck.carte.effet.effetpoint.AjoutePoints
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.strategie.ActionCle
import com.chateaucombo.strategie.genetique.ExtracteurFeatures
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExtracteurFeaturesTest {

    private fun villageoisSimple(nom: String = "V", cout: Int = 0, blasons: List<Blason> = listOf(Blason.PAYSAN)): Carte =
        Villageois(nom = nom, cout = cout, blasons = blasons, effets = Effets(), effetScore = EffetScoreVide)

    private fun chatelainAvecPoints(points: Int): Carte =
        Chatelain(nom = "C", cout = 0, blasons = listOf(Blason.NOBLE), effets = Effets(), effetScore = AjoutePoints(points))

    @Test
    fun `retourne exactement NB_FEATURES valeurs`() {
        val features = ExtracteurFeatures.extrait(
            joueurAvant = Joueur(id = 0),
            carte = villageoisSimple(),
            position = Position.MILIEUMILIEU,
            actionCle = ActionCle.RIEN,
            tour = 1,
        )

        assertThat(features).hasSize(ExtracteurFeatures.NB_FEATURES)
    }

    @Test
    fun `marque la position centre quand la carte y est placee`() {
        val features = ExtracteurFeatures.extrait(
            joueurAvant = Joueur(id = 0),
            carte = villageoisSimple(),
            position = Position.MILIEUMILIEU,
            actionCle = ActionCle.RIEN,
            tour = 1,
        )

        // index 9 = position centre, 7 = coin, 8 = bord
        assertThat(features[9]).isEqualTo(1f)
        assertThat(features[7]).isEqualTo(0f)
        assertThat(features[8]).isEqualTo(0f)
    }

    @Test
    fun `marque les coins et les bords correctement`() {
        val joueur = Joueur(id = 0)

        val coin = ExtracteurFeatures.extrait(joueur, villageoisSimple(), Position.HAUTGAUCHE, ActionCle.RIEN, tour = 1)
        val bord = ExtracteurFeatures.extrait(joueur, villageoisSimple(), Position.HAUTMILIEU, ActionCle.RIEN, tour = 1)

        assertThat(coin[7]).isEqualTo(1f)  // coin
        assertThat(coin[8]).isEqualTo(0f)
        assertThat(coin[9]).isEqualTo(0f)
        assertThat(bord[7]).isEqualTo(0f)
        assertThat(bord[8]).isEqualTo(1f)  // bord
        assertThat(bord[9]).isEqualTo(0f)
    }

    @Test
    fun `distingue chatelain villageois et carte verso`() {
        val joueur = Joueur(id = 0)
        val featuresVillageois = ExtracteurFeatures.extrait(joueur, villageoisSimple(), Position.MILIEUMILIEU, ActionCle.RIEN, tour = 1)
        val featuresChatelain = ExtracteurFeatures.extrait(joueur, chatelainAvecPoints(0), Position.MILIEUMILIEU, ActionCle.RIEN, tour = 1)

        // index 4 = chatelain, 5 = villageois, 6 = verso
        assertThat(featuresVillageois[4]).isEqualTo(0f)
        assertThat(featuresVillageois[5]).isEqualTo(1f)
        assertThat(featuresChatelain[4]).isEqualTo(1f)
        assertThat(featuresChatelain[5]).isEqualTo(0f)
    }

    @Test
    fun `marque la penalite d'action cle quand une cle est depensee`() {
        val joueur = Joueur(id = 0)
        val sansAction = ExtracteurFeatures.extrait(joueur, villageoisSimple(), Position.MILIEUMILIEU, ActionCle.RIEN, tour = 1)
        val avecAction = ExtracteurFeatures.extrait(joueur, villageoisSimple(), Position.MILIEUMILIEU, ActionCle.CHANGE_DECK, tour = 1)

        // index 14 = penalite action cle
        assertThat(sansAction[14]).isEqualTo(0f)
        assertThat(avecAction[14]).isEqualTo(1f)
    }

    @Test
    fun `compte le gain de score final pour une carte qui ajoute des points`() {
        val features = ExtracteurFeatures.extrait(
            joueurAvant = Joueur(id = 0),
            carte = chatelainAvecPoints(10),
            position = Position.MILIEUMILIEU,
            actionCle = ActionCle.RIEN,
            tour = 1,
        )

        // index 0 = gainScoreFinal / 10 ; carte qui ajoute 10 pts => 1.0
        assertThat(features[0]).isEqualTo(1f)
    }

    @Test
    fun `compte les blasons identiques deja en rangee`() {
        val villageoisExistant = villageoisSimple(nom = "Existant", blasons = listOf(Blason.PAYSAN, Blason.PAYSAN))
        val tableau = Tableau(mutableListOf(CartePositionee(villageoisExistant, Position.MILIEUMILIEU)))
        val joueur = Joueur(id = 0, tableau = tableau)
        val carteAplacer = villageoisSimple(blasons = listOf(Blason.PAYSAN))

        val features = ExtracteurFeatures.extrait(joueur, carteAplacer, Position.MILIEUGAUCHE, ActionCle.RIEN, tour = 2)

        // index 10 = nb de blasons identiques en rangée (PAYSAN dans la carte à placer × 2 PAYSAN dans la rangée)
        assertThat(features[10]).isEqualTo(2f)
    }

    @Test
    fun `cout d'opportunite est positif quand le joueur a une bourse a remplir`() {
        val carteBourse = Villageois(
            nom = "B", cout = 0, blasons = listOf(Blason.PAYSAN),
            effets = Effets(), effetScore = EffetScoreVide, bourse = BourseScore(taille = 5),
        )
        val tableau = Tableau(mutableListOf(CartePositionee(carteBourse, Position.MILIEUMILIEU)))
        val joueur = Joueur(id = 0, or = 5, tableau = tableau)
        val carteCouteuse = villageoisSimple(cout = 4)

        val features = ExtracteurFeatures.extrait(joueur, carteCouteuse, Position.MILIEUGAUCHE, ActionCle.RIEN, tour = 2)

        // index 2 = coutOpportunite / 10 ; on dépense 4 or qui auraient pu remplir la bourse → 4 * 2 / 10 = 0.8
        assertThat(features[2]).isEqualTo(0.8f)
    }
}
