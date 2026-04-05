package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.effet.effetplacement.AjouteClePourChaqueBlason
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTDROITE
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AjouteCleParBlasonEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @ParameterizedTest
    @CsvSource(
        value = [
            "NOBLE",
            "RELIGIEUX",
            "ERUDIT",
            "MILITAIRE",
            "ARTISAN",
            "PAYSAN",
        ]
    )
    fun `doit ajouter autant de cles par carte avec le blason dans les cartes positionnees`(blason: Blason) {
        val cleInitial = 2
        val tableau = tableauAvecTroisCartesAvecLeBlason(blason)
        val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableau)
        val carte =
            villageois(
                effets = Effets(
                    effets = listOf(
                        AjouteClePourChaqueBlason(blason = blason)
                    )
                )
            )
        val context = EffetContext(
            joueurActuel = joueur,
            joueurs = emptyList(),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitial + 3)
    }

    private fun tableauAvecTroisCartesAvecLeBlason(blason: Blason): Tableau =
        Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(carte = villageois(blasons = listOf(blason)), position = HAUTGAUCHE),
                CartePositionee(carte = villageois(blasons = listOf(blason)), position = HAUTMILIEU),
                CartePositionee(carte = villageois(blasons = listOf(blason)), position = HAUTDROITE),
            )
        )

    @ParameterizedTest
    @CsvSource(
        value = [
            "NOBLE",
            "RELIGIEUX",
            "ERUDIT",
            "MILITAIRE",
            "ARTISAN",
            "PAYSAN",
        ]
    )
    fun `doit compter deux fois les cartes avec deux blasons identiques`(blason: Blason) {
        val cleInitial = 2
        val tableau = tableauAvecTroisCartesAvecDoubleBlason(blason)
        val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableau)
        val carte = villageois(
            effets = Effets(
                effets = listOf(
                    AjouteClePourChaqueBlason(blason = blason)
                )
            )
        )
        val context = EffetContext(
            joueurActuel = joueur,
            joueurs = emptyList(),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitial + 3 * 2)
    }

    private fun tableauAvecTroisCartesAvecDoubleBlason(blason: Blason): Tableau =
        Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(
                    carte = villageois(blasons = listOf(blason, blason)),
                    position = HAUTGAUCHE
                ),
                CartePositionee(
                    carte = villageois(blasons = listOf(blason, blason)),
                    position = HAUTMILIEU
                ),
                CartePositionee(
                    carte = villageois(blasons = listOf(blason, blason)),
                    position = HAUTDROITE
                ),
            )
        )
}
