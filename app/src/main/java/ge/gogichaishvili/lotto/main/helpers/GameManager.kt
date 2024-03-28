package ge.gogichaishvili.lotto.main.helpers

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.main.enums.GameOverStatusEnum
import ge.gogichaishvili.lotto.main.presentation.fragments.CustomDialogFragment

class GameManager() {

    fun checkGameResult(gameOverStatus: GameOverStatusEnum, context: Context, onDismissed: (() -> Unit)? = null) {
        val dialog = CustomDialogFragment().apply {
            arguments = bundleOf("DATA" to when (gameOverStatus) {
                GameOverStatusEnum.PLAYER_WIN -> context.getString(R.string.win)
                GameOverStatusEnum.OPPONENT_WIN -> context.getString(R.string.lose)
                GameOverStatusEnum.Draw -> context.getString(R.string.draw)
            })
        }
        dialog.setOnDialogDismissListener {
            onDismissed?.invoke()
        }
        dialog.show((context as FragmentActivity).supportFragmentManager, "CustomDialogFragment")
    }

}
