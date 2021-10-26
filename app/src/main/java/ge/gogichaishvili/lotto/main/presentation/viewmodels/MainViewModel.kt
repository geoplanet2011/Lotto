package ge.gogichaishvili.lotto.main.presentation.viewmodels

import androidx.lifecycle.ViewModel
import ge.gogichaishvili.lotto.main.BagManager
import ge.gogichaishvili.lotto.main.data.BagModel


class MainViewModel() : ViewModel() {


    fun getNumberFromBag(): BagModel {
        return BagManager.getNumberFromBag()
    }


}