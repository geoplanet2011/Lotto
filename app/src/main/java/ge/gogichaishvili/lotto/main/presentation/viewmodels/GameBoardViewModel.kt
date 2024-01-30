package ge.gogichaishvili.lotto.main.presentation.viewmodels

import android.content.Context
import android.widget.LinearLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ge.gogichaishvili.lotto.app.tools.SingleLiveEvent
import ge.gogichaishvili.lotto.main.helpers.LottoCardManager
import ge.gogichaishvili.lotto.main.helpers.LottoStonesManager
import ge.gogichaishvili.lotto.main.models.LottoCardModel
import ge.gogichaishvili.lotto.main.models.LottoDrawResult
import ge.gogichaishvili.lotto.main.presentation.viewmodels.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameBoardViewModel (
    //private val pref: SharedPreferenceManager
) : BaseViewModel() {

    private val _requestStateLiveData = SingleLiveEvent<LottoDrawResult>()
    val requestStateLiveData: LiveData<LottoDrawResult> get() = _requestStateLiveData

    private val _generateCardRequestStateLiveData = SingleLiveEvent<LottoCardModel>()
    val generateLottoCardRequestStateLiveData: LiveData<LottoCardModel> get() = _generateCardRequestStateLiveData

    fun getNumberFromBag() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            val result = LottoStonesManager.getNumberFromBag()
            _requestStateLiveData.postValue(result)
        }
    }

    fun generateCard(context: Context, linearLayout: LinearLayout) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            val result = LottoCardManager.generateCard(context, linearLayout, _requestStateLiveData)
            _generateCardRequestStateLiveData.postValue(result)
        }
    }

}