package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason
import com.chateaucombo.effet.model.AjouteOrPourChaqueBlason
import com.chateaucombo.effet.model.EffetContext
import com.chateaucombo.effet.model.Effets
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AjouteOrParBlasonEffetTest : EffetTestBase() {
    @ParameterizedTest
    @CsvSource(
        value = [
            "1,NOBLE",
            "2,NOBLE",
            "1,RELIGIEUX",
            "2,RELIGIEUX",
            "1,ERUDIT",
            "2,ERUDIT",
            "1,MILITAIRE",
            "2,MILITAIRE",
            "1,ARTISAN",
            "2,ARTISAN",
            "1,PAYSAN",
            "2,PAYSAN"
        ]
    )
    fun `doit ajouter autant d'or par carte avec le blason dans les cartes positionnees`(
        orParBlason: Int,
        blason: Blason
    ) {
        val orInitial = 2
        val tableau = tableauAvecTroisCartesAvecLeBlason(blason)
        val joueur = Joueur(id = 1, or = orInitial, tableau = tableau)
        val carte =
            villageois(
                effets = Effets(
                    effets = listOf(
                        AjouteOrPourChaqueBlason(
                            orParBlason = orParBlason,
                            blason = blason
                        )
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

        assertThat(joueur.or).isEqualTo(orInitial + 3 * orParBlason)
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
            "1,NOBLE",
            "2,NOBLE",
            "1,RELIGIEUX",
            "2,RELIGIEUX",
            "1,ERUDIT",
            "2,ERUDIT",
            "1,MILITAIRE",
            "2,MILITAIRE",
            "1,ARTISAN",
            "2,ARTISAN",
            "1,PAYSAN",
            "2,PAYSAN"
        ]
    )
    fun `doit compter deux fois les cartes avec deux blasons identiques`(orParBlason: Int, blason: Blason) {
        val orInitial = 2
        val tableau = tableauAvecTroisCartesAvecDoubleBlason(blason)
        val joueur = Joueur(id = 1, or = orInitial, tableau = tableau)
        val carte = villageois(
            effets = Effets(
                effets = listOf(
                    AjouteOrPourChaqueBlason(
                        orParBlason = orParBlason,
                        blason = blason
                    )
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

        assertThat(joueur.or).isEqualTo(orInitial + 3 * 2 * orParBlason)
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
