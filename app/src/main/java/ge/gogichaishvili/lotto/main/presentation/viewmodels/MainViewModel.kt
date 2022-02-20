package ge.gogichaishvili.lotto.main.presentation.viewmodels

import android.content.Context
import android.widget.LinearLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ge.gogichaishvili.lotto.app.tools.SingleLiveEvent
import ge.gogichaishvili.lotto.app.viewmodels.base.BaseViewModel
import ge.gogichaishvili.lotto.main.helpers.LottoStonesManager
import ge.gogichaishvili.lotto.main.helpers.LottoCardManager
import ge.gogichaishvili.lotto.main.data.LottoStonesModel
import ge.gogichaishvili.lotto.main.data.LottoCardModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel() : BaseViewModel() {

    private val _requestStateLiveData = SingleLiveEvent<LottoStonesModel>()
    val requestStateLiveData: LiveData<LottoStonesModel> get() = _requestStateLiveData

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