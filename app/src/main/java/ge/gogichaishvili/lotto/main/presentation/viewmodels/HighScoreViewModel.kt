package ge.gogichaishvili.lotto.main.presentation.viewmodels

import ge.gogichaishvili.lotto.app.tools.SharedPreferenceManager
import ge.gogichaishvili.lotto.main.helpers.RatingManager
import ge.gogichaishvili.lotto.main.presentation.viewmodels.base.BaseViewModel

class HighScoreViewModel(
    private val pref: SharedPreferenceManager,
    private val ratingManager: RatingManager
) : BaseViewModel() {

    fun initRatingManager(score: Int) {
        ratingManager.init(score)
    }

    fun getWins(): String = pref.getGameStatistics().win.toString()

    fun getLoses(): String = pref.getGameStatistics().lose.toString()

    fun getBalance(): String = pref.getPlayerBalance().toString()

    fun getStars(): List<RatingManager.Star> = ratingManager.getStars()

    fun getRating() : Int {
        return 55
    }
}