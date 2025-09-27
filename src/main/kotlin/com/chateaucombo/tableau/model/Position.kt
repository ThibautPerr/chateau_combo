package com.chateaucombo.tableau.model

import com.chateaucombo.tableau.model.PositionHorizontale.DROITE
import com.chateaucombo.tableau.model.PositionHorizontale.GAUCHE
import com.chateaucombo.tableau.model.PositionVerticale.BAS
import com.chateaucombo.tableau.model.PositionVerticale.HAUT

enum class Position(
    val positionVerticale: PositionVerticale,
    val positionHorizontale: PositionHorizontale
) {
    HAUTGAUCHE(positionVerticale = HAUT, positionHorizontale = GAUCHE),
    HAUTMILIEU(positionVerticale = HAUT, positionHorizontale = PositionHorizontale.MILIEU),
    HAUTDROITE(positionVerticale = HAUT, positionHorizontale = DROITE),
    MILIEUGAUCHE(positionVerticale = PositionVerticale.MILIEU, positionHorizontale = GAUCHE),
    MILIEUMILIEU(positionVerticale = PositionVerticale.MILIEU, positionHorizontale = PositionHorizontale.MILIEU),
    MILIEUDROITE(positionVerticale = PositionVerticale.MILIEU, positionHorizontale = DROITE),
    BASGAUCHE(positionVerticale = BAS, positionHorizontale = GAUCHE),
    BASMILIEU(positionVerticale = BAS, positionHorizontale = PositionHorizontale.MILIEU),
    BASDROITE(positionVerticale = BAS, positionHorizontale = DROITE);

    fun positionsAdjacentes() =
        listOfNotNull(positionAGauche(), positionADroite(), positionEnHaut(), positionEnBas())

    fun positionAGauche() =
        Position.entries.firstOrNull {
            it.positionVerticale == this.positionVerticale && it.positionHorizontale.x == this.positionHorizontale.x - 1
        }

    fun positionADroite() =
        Position.entries.firstOrNull {
            it.positionVerticale == this.positionVerticale && it.positionHorizontale.x == this.positionHorizontale.x + 1
        }

    fun positionEnHaut() =
        Position.entries.firstOrNull {
            it.positionHorizontale == this.positionHorizontale && it.positionVerticale.y == this.positionVerticale.y - 1
        }

    fun positionEnBas() =
        Position.entries.firstOrNull {
            it.positionHorizontale == this.positionHorizontale && it.positionVerticale.y == this.positionVerticale.y + 1
        }
}