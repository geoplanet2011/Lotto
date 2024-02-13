package ge.gogichaishvili.lotto.main.helpers

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.SharedPreferenceManager
import ge.gogichaishvili.lotto.app.tools.Tools
import ge.gogichaishvili.lotto.main.enums.GameOverStatusEnum
import ge.gogichaishvili.lotto.main.presentation.fragments.CustomDialogFragment

class GameManager() {

    fun checkGameResult(gameOverStatus: GameOverStatusEnum, context: Context) {

        val dialog = CustomDialogFragment()
        val pref = SharedPreferenceManager(context)
        val gameStatistic = pref.getGameStatistics()

        when (gameOverStatus) {

            GameOverStatusEnum.PLAYER_WIN -> {
                Tools.playSound(context, R.raw.applause)
                pref.saveGameStatistics(gameStatistic.lose, gameStatistic.win.plus(1))
                dialog.arguments = bundleOf("DATA" to context.getString(R.string.win))
            }

            GameOverStatusEnum.OPPONENT_WIN -> {
                Tools.playSound(context, R.raw.busy)
                pref.saveGameStatistics(gameStatistic.lose.plus(1), gameStatistic.win)
                dialog.arguments = bundleOf("DATA" to context.getString(R.string.lose))
            }

            GameOverStatusEnum.Draw -> {
                dialog.arguments = bundleOf("DATA" to context.getString(R.string.draw))
            }

        }

        dialog.show(
            (context as FragmentActivity).supportFragmentManager,
            "CustomDialogFragment"
        )

    }

}
