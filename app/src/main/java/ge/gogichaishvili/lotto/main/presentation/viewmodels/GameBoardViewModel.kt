package ge.gogichaishvili.lotto.main.presentation.viewmodels

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ge.gogichaishvili.lotto.app.tools.SharedPreferenceManager
import ge.gogichaishvili.lotto.app.tools.SingleLiveEvent
import ge.gogichaishvili.lotto.main.helpers.LottoCardManager
import ge.gogichaishvili.lotto.main.helpers.LottoStonesManager
import ge.gogichaishvili.lotto.main.helpers.OpponentCardManager
import ge.gogichaishvili.lotto.main.helpers.OpponentManager
import ge.gogichaishvili.lotto.main.models.LottoDrawResult
import ge.gogichaishvili.lotto.main.models.OpponentAvatarModel
import ge.gogichaishvili.lotto.main.models.PlayerData
import ge.gogichaishvili.lotto.main.presentation.viewmodels.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameBoardViewModel(
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

    fun redrawCard(context: Context, linearLayout: LinearLayout) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            lottoCardManager.redrawCards(context, linearLayout, _requestStateLiveData)
        }
    }

    fun generateOpponentCard() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            opponentCardManager.generateBotCards()
        }
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

    fun getPlayerInfo(): PlayerData {
        return pref.getPlayerInfo()
    }

    fun getOpponentInfo(): OpponentAvatarModel {
        return opponentManager.getOpponentInfo()
    }

    fun disableAllViewsInViewGroup(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            child.isEnabled = false
            if (child is ViewGroup) {
                disableAllViewsInViewGroup(child)
            }
        }
    }

    fun enableAllViewsInViewGroup(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            child.isEnabled = true
            if (child is ViewGroup) {
                enableAllViewsInViewGroup(child)
            }
        }
    }


}