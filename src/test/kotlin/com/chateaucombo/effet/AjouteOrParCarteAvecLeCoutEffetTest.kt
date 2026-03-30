package com.chateaucombo.effet

import com.chateaucombo.effet.model.AjouteOrParCarteAvecLeCout
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AjouteOrParCarteAvecLeCoutEffetTest : EffetTestBase() {
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
