package ge.gogichaishvili.lotto.main.presentation.viewmodels

import ge.gogichaishvili.lotto.app.tools.SharedPreferenceManager
import ge.gogichaishvili.lotto.main.helpers.RatingManager
import ge.gogichaishvili.lotto.main.presentation.viewmodels.base.BaseViewModel
import kotlin.math.ln1p

class HighScoreViewModel(
    private val pref: SharedPreferenceManager,
    private val ratingManager: RatingManager
) : BaseViewModel() {

    fun initRatingManager(score: Int) {
        ratingManager.init(score)
    }

    fun getWins(): Int = pref.getGameStatistics().win

    fun getLoses(): Int = pref.getGameStatistics().lose

    fun getBalance(): Int = pref.getPlayerBalance()

    fun getStars(): List<RatingManager.Star> = ratingManager.getStars()

    fun calculatePlayerRating(wins: Int, losses: Int, moneyBalance: Int): Int {

        val winWeight = 2.0
        val lossWeight = 1.0
        val moneyWeight = 0.05
        val maxPoints = 100.0

        val pointsFromWinsAndLosses =
            ((wins * winWeight) - (losses * lossWeight)).coerceIn(0.0, maxPoints)

        val normalizedMoneyBalance = ln1p(moneyBalance.toDouble()) * moneyWeight
        val maxMoneyContribution = 25.0
        val moneyBalanceContribution = normalizedMoneyBalance.coerceAtMost(maxMoneyContribution)

        val totalPoints = pointsFromWinsAndLosses + moneyBalanceContribution
        val maxTotalPoints = maxPoints + maxMoneyContribution
        var finalRating = (totalPoints / maxTotalPoints) * 100.0

        finalRating = finalRating.coerceIn(1.0, 100.0)

        return finalRating.toInt()
    }


}