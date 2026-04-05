package com.chateaucombo.tableau

import com.chateaucombo.tableau.PositionVerticale.BAS
import com.chateaucombo.tableau.PositionVerticale.HAUT

enum class Position(
    val positionVerticale: PositionVerticale,
    val positionHorizontale: PositionHorizontale
) {
    HAUTGAUCHE(positionVerticale = HAUT, positionHorizontale = PositionHorizontale.GAUCHE),
    HAUTMILIEU(positionVerticale = HAUT, positionHorizontale = PositionHorizontale.MILIEU),
    HAUTDROITE(positionVerticale = HAUT, positionHorizontale = PositionHorizontale.DROITE),
    MILIEUGAUCHE(positionVerticale = PositionVerticale.MILIEU, positionHorizontale = PositionHorizontale.GAUCHE),
    MILIEUMILIEU(positionVerticale = PositionVerticale.MILIEU, positionHorizontale = PositionHorizontale.MILIEU),
    MILIEUDROITE(positionVerticale = PositionVerticale.MILIEU, positionHorizontale = PositionHorizontale.DROITE),
    BASGAUCHE(positionVerticale = BAS, positionHorizontale = PositionHorizontale.GAUCHE),
    BASMILIEU(positionVerticale = BAS, positionHorizontale = PositionHorizontale.MILIEU),
    BASDROITE(positionVerticale = BAS, positionHorizontale = PositionHorizontale.DROITE);

    fun positionsAdjacentes() =
        listOfNotNull(positionAGauche(), positionADroite(), positionEnHaut(), positionEnBas())

    fun positionAGauche() =
        entries.firstOrNull {
            it.positionVerticale == this.positionVerticale && it.positionHorizontale.x == this.positionHorizontale.x - 1
        }

    fun positionADroite() =
        entries.firstOrNull {
            it.positionVerticale == this.positionVerticale && it.positionHorizontale.x == this.positionHorizontale.x + 1
        }

    fun positionEnHaut() =
        entries.firstOrNull {
            it.positionHorizontale == this.positionHorizontale && it.positionVerticale.y == this.positionVerticale.y - 1
        }

    fun positionEnBas() =
        entries.firstOrNull {
            it.positionHorizontale == this.positionHorizontale && it.positionVerticale.y == this.positionVerticale.y + 1
        }
}