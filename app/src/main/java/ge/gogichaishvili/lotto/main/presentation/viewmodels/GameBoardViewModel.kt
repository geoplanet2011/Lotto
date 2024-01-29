package ge.gogichaishvili.lotto.main.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ge.gogichaishvili.lotto.app.tools.SingleLiveEvent
import ge.gogichaishvili.lotto.main.helpers.LottoStonesManager
import ge.gogichaishvili.lotto.main.models.LottoStonesModel
import ge.gogichaishvili.lotto.main.presentation.viewmodels.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameBoardViewModel (
    //private val pref: SharedPreferenceManager
) : BaseViewModel() {

    private val _requestStateLiveData = SingleLiveEvent<LottoStonesModel>()
    val requestStateLiveData: LiveData<LottoStonesModel> get() = _requestStateLiveData


    fun getNumberFromBag() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            val result = LottoStonesManager.getNumberFromBag()
            _requestStateLiveData.postValue(result)
        }
    }

}