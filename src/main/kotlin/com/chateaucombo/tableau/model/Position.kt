package com.chateaucombo.tableau.model

import com.chateaucombo.tableau.model.PositionHorizontale.DROITE
import com.chateaucombo.tableau.model.PositionHorizontale.GAUCHE
import com.chateaucombo.tableau.model.PositionVerticale.BAS
import com.chateaucombo.tableau.model.PositionVerticale.HAUT

enum class Position(
    private val positionVerticale: PositionVerticale,
    private val positionHorizontale: PositionHorizontale
) {
    HAUTGAUCHE(positionVerticale = HAUT, positionHorizontale = GAUCHE),
    HAUTMILIEU(positionVerticale = HAUT, positionHorizontale = PositionHorizontale.MILIEU),
    HAUTDROITE(positionVerticale = HAUT, positionHorizontale = DROITE),
    MILIEUGAUCHE(positionVerticale = PositionVerticale.MILIEU, positionHorizontale = GAUCHE),
    MILIEUMILIEU(positionVerticale = PositionVerticale.MILIEU, positionHorizontale = PositionHorizontale.MILIEU),
    MILIEUDROITE(positionVerticale = PositionVerticale.MILIEU, positionHorizontale = DROITE),
    BASGAUCHE(positionVerticale = BAS, positionHorizontale = GAUCHE),
    BASMILIEU(positionVerticale = BAS, positionHorizontale = PositionHorizontale.MILIEU),
    BASDROITE(positionVerticale = BAS, positionHorizontale = DROITE),
}