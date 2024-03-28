package ge.gogichaishvili.lotto.main.presentation.viewmodels

import android.content.Context
import android.widget.LinearLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ge.gogichaishvili.lotto.app.tools.SharedPreferenceManager
import ge.gogichaishvili.lotto.app.tools.SingleLiveEvent
import ge.gogichaishvili.lotto.main.enums.GameOverStatusEnum
import ge.gogichaishvili.lotto.main.helpers.GameManager
import ge.gogichaishvili.lotto.main.helpers.LottoCardManager
import ge.gogichaishvili.lotto.main.helpers.LottoStonesManager
import ge.gogichaishvili.lotto.main.models.LottoDrawResult
import ge.gogichaishvili.lotto.main.presentation.viewmodels.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val lottoManager: LottoStonesManager,
    val lottoCardManager: LottoCardManager,
    private val pref: SharedPreferenceManager
) : BaseViewModel() {

    private val _requestStateLiveData = SingleLiveEvent<LottoDrawResult>()
    val requestStateLiveData: LiveData<LottoDrawResult> get() = _requestStateLiveData

    private val _requestStateStonesLiveData = SingleLiveEvent<LottoDrawResult>()
    val requestStateStonesLiveData: LiveData<LottoDrawResult> get() = _requestStateStonesLiveData

    private val _dismissLiveData = SingleLiveEvent<Unit>()
    val dismissLiveData: LiveData<Unit> get() = _dismissLiveData

    fun getNumberFromBag() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            val result = lottoManager.getNumberFromBag()
            _requestStateStonesLiveData.postValue(result)
        }
    }

    fun getNumberFromServer(result: LottoDrawResult) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
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
        gameManager.checkGameResult(gameOverStatus, context) {
            _dismissLiveData.postValue(Unit)
        }
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

    val lineCompletionEvent = MutableLiveData<Unit>()
    val cardCompletionEvent = MutableLiveData<Unit>()

    init {
        setupLottoCardManager()
    }

    private fun setupLottoCardManager() {
        lottoCardManager.setOnLineCompleteListener {
            lineCompletionEvent.postValue(Unit)
        }

        lottoCardManager.setOnCardCompleteListener {
            cardCompletionEvent.postValue(Unit)
        }
    }

}