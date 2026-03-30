package com.chateaucombo.effet

import com.chateaucombo.deck.model.Blason.ERUDIT
import com.chateaucombo.deck.model.Blason.RELIGIEUX
import com.chateaucombo.effet.model.AjouteCleParCarteAvecNbBlason
import com.chateaucombo.effet.model.EffetContext
import com.chateaucombo.effet.model.Effets
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.tableau.model.CartePositionee
import com.chateaucombo.tableau.model.Position.HAUTDROITE
import com.chateaucombo.tableau.model.Position.HAUTGAUCHE
import com.chateaucombo.tableau.model.Position.HAUTMILIEU
import com.chateaucombo.tableau.model.Position.MILIEUGAUCHE
import com.chateaucombo.tableau.model.Position.MILIEUMILIEU
import com.chateaucombo.tableau.model.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AjouteCleParCarteAvecNbBlasonEffetTest : EffetTestBase() {
    @Test
    fun `doit ajouter autant de cles que de cartes avec un seul blason`() {
        val cleInitial = 2
        val tableau = tableauAvecTroisCartesAvecUnSeulBlason()
        val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableau)
        val carte =
            villageois(
                effets = Effets(
                    effets = listOf(
                        AjouteCleParCarteAvecNbBlason(nbBlason = 1)
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

    private fun tableauAvecTroisCartesAvecUnSeulBlason() =
        Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(
                    carte = villageois(blasons = listOf(RELIGIEUX)),
                    position = HAUTGAUCHE
                ),
                CartePositionee(
                    carte = villageois(blasons = listOf(RELIGIEUX)),
                    position = HAUTMILIEU
                ),
                CartePositionee(
                    carte = villageois(blasons = listOf(RELIGIEUX)),
                    position = HAUTDROITE
                ),
                CartePositionee(
                    carte = villageois(blasons = listOf(RELIGIEUX, ERUDIT)),
                    position = MILIEUGAUCHE
                ),
            )
        )

    @Test
    fun `doit ajouter autant de cles que de cartes avec deux blason`() {
        val cleInitial = 2
        val tableau = tableauAvecTroisCartesAvecDeuxBlasons()
        val joueur = Joueur(id = 1, cle = cleInitial, tableau = tableau)
        val carte =
            villageois(
                effets = Effets(
                    effets = listOf(
                        AjouteCleParCarteAvecNbBlason(nbBlason = 2)
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

    private fun tableauAvecTroisCartesAvecDeuxBlasons() =
        Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(
                    carte = villageois(blasons = listOf(RELIGIEUX, ERUDIT)),
                    position = HAUTGAUCHE
                ),
                CartePositionee(
                    carte = villageois(blasons = listOf(RELIGIEUX, ERUDIT)),
                    position = HAUTMILIEU
                ),
                CartePositionee(
                    carte = villageois(blasons = listOf(RELIGIEUX, ERUDIT)),
                    position = HAUTDROITE
                ),
                CartePositionee(
                    carte = villageois(blasons = listOf(RELIGIEUX)),
                    position = MILIEUGAUCHE
                ),
            )
        )
}
