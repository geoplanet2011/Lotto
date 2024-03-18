package ge.gogichaishvili.lotto.main.presentation.viewmodels

import android.content.Context
import android.widget.LinearLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ge.gogichaishvili.lotto.app.tools.SharedPreferenceManager
import ge.gogichaishvili.lotto.app.tools.SingleLiveEvent
import ge.gogichaishvili.lotto.main.enums.GameOverStatusEnum
import ge.gogichaishvili.lotto.main.helpers.GameManager
import ge.gogichaishvili.lotto.main.helpers.LottoCardManager
import ge.gogichaishvili.lotto.main.helpers.LottoStonesManager
import ge.gogichaishvili.lotto.main.helpers.OpponentCardManager
import ge.gogichaishvili.lotto.main.helpers.OpponentManager
import ge.gogichaishvili.lotto.main.models.LottoDrawResult
import ge.gogichaishvili.lotto.main.presentation.viewmodels.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel (
    private val lottoManager: LottoStonesManager,
    val lottoCardManager: LottoCardManager,
    private val pref: SharedPreferenceManager,
    private val opponentManager: OpponentManager,
    private val opponentCardManager: OpponentCardManager
) : BaseViewModel() {

    private val _requestStateLiveData = SingleLiveEvent<LottoDrawResult>()
    val requestStateLiveData: LiveData<LottoDrawResult> get() = _requestStateLiveData

    fun getNumberFromBag() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            val result = lottoManager.getNumberFromBag()
            _requestStateLiveData.postValue(result)
        }
    }

    fun generateCard(context: Context, linearLayout: LinearLayout) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            lottoCardManager.generateCard(context, linearLayout, _requestStateLiveData)
        }
    }

    fun checkGameResult(gameOverStatus: GameOverStatusEnum, context: Context) {
        val gameManager = GameManager()
        gameManager.checkGameResult(gameOverStatus, context)
    }

    fun isHintEnabled(): Boolean {
        return pref.isEnabledHint()
    }

    fun resetManagers() {
        lottoManager.resetBag()
        lottoCardManager.resetAll()
    }

    fun isSoundEnabled(): Boolean {
        return pref.isEnabledSound()
    }

    fun getSelectedLanguage(): String {
        return pref.getSelectedLanguageCode()
    }

    fun getGameSpeed(): Long {
        return pref.getGameSpeed()
    }

    fun bagShuffle () {
        lottoManager.shuffle()
    }
}