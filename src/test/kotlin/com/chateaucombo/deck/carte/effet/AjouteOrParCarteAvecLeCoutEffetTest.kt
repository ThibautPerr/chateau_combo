package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetplacement.AjouteOrParCarteAvecLeCout
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.HAUTDROITE
import com.chateaucombo.tableau.Position.HAUTGAUCHE
import com.chateaucombo.tableau.Position.HAUTMILIEU
import com.chateaucombo.tableau.Position.MILIEUGAUCHE
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import com.chateaucombo.tableau.Tableau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AjouteOrParCarteAvecLeCoutEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @ParameterizedTest
    @CsvSource(
        "1,0",
        "3,0",
        "1,4",
        "3,4",
    )
    fun `doit ajouter autant de cles que de cartes avec un seul blason`(or: Int, cout: Int) {
        val orInitial = 2
        val tableau = tableauAvecTroisCartesAvecLeCout(cout)
        val joueur = Joueur(id = 1, or = orInitial, tableau = tableau)
        val carte = villageois(
            effets = Effets(
                effets = listOf(
                    AjouteOrParCarteAvecLeCout(orParCarte = or, cout = cout)
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

        assertThat(joueur.or).isEqualTo(orInitial + or * 3)
    }

    private fun tableauAvecTroisCartesAvecLeCout(cout: Int) =
        Tableau(
            cartesPositionees = mutableListOf(
                CartePositionee(
                    carte = villageois(cout = cout),
                    position = HAUTGAUCHE
                ),
                CartePositionee(
                    carte = villageois(cout = cout),
                    position = HAUTMILIEU
                ),
                CartePositionee(
                    carte = villageois(cout = cout),
                    position = HAUTDROITE
                ),
                CartePositionee(
                    carte = villageois(cout = -1),
                    position = MILIEUGAUCHE
                ),
            )
        )
}
