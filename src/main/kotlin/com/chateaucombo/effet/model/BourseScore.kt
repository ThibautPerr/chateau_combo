package com.chateaucombo.effet.model

data class BourseScore(val taille: Int) : EffetScore {
    var orDepose: Int = 0
    override fun score(context: ScoreContext): Int = 0
}
