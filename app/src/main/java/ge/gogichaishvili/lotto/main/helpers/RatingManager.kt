package ge.gogichaishvili.lotto.main.helpers

class RatingManager {

    private var stars = mutableListOf<Star>()

    companion object {
        const val RATING_MIN = 1
        const val RATING_MAX = 100
        const val RATING_INCREASE_UNIT = 10
        const val RATING_STARS_COUNT = 5
    }

    fun getStars(): List<Star> = stars

    fun init(rating: Int) {

        fillStars()

        val validatedRating = when {
            rating < RATING_MIN -> RATING_MIN
            rating > RATING_MAX -> RATING_MAX
            else -> rating
        }

        setStarsByHighScore(validatedRating.toDouble())
    }

    private var index = 1
    private fun fillStars() {
        for (i in 0 until RATING_STARS_COUNT) {
            val star = Star()
            star.halfScore = (index * RATING_INCREASE_UNIT + i * RATING_INCREASE_UNIT).toDouble()
            star.fullScore = ((index * RATING_INCREASE_UNIT + i * RATING_INCREASE_UNIT) + RATING_INCREASE_UNIT).toDouble()
            stars.add(star)
            index++
        }
    }

    private fun setStarsByHighScore(highScore: Double) {
        stars.forEach { star ->
            when {
                highScore >= star.halfScore && highScore < star.fullScore -> star.state = Star.StarState.HalfStar
                highScore >= star.fullScore -> star.state = Star.StarState.FullStar
            }
        }
    }

    class Star {
        private var image: String = ""

        var halfScore: Double = 0.0
        var fullScore: Double = 0.0

        var state: StarState = StarState.EmptyStar
            set(value) {
                field = value
                image = "${state.name}.png"
            }

        enum class StarState {
            EmptyStar, HalfStar, FullStar
        }
    }
}
