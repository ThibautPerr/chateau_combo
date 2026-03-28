package com.chateaucombo.effet.model

data class BourseScore(val taille: Int) : EffetScore {
    override fun score(context: ScoreContext): Int = 0
}
