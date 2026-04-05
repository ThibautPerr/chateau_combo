package com.chateaucombo.deck.carte.effet

import com.chateaucombo.deck.carte.effet.effetplacement.AjouteCle
import com.chateaucombo.joueur.Joueur
import com.chateaucombo.tableau.CartePositionee
import com.chateaucombo.tableau.Position.MILIEUMILIEU
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AjouteCleEffetTest : com.chateaucombo.deck.carte.effet.EffetTestBase() {
    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4])
    fun `doit ajouter autant de cles au joueur actuel`(cle: Int) {
        val cleInitiale = 2
        val joueur = Joueur(id = 1, cle = cleInitiale)
        val carte = villageois(effets = Effets(effets = listOf(AjouteCle(cle))))
        val context = EffetContext(
            joueurActuel = joueur,
            joueurs = emptyList(),
            cartePositionee = CartePositionee(carte = carte, position = MILIEUMILIEU),
            decks = emptyList()
        )

        carte.effets.effets.first().apply(context)

        assertThat(joueur.cle).isEqualTo(cleInitiale + cle)
    }
}
