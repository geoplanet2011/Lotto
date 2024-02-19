package ge.gogichaishvili.lotto.main.presentation.viewmodels

import ge.gogichaishvili.lotto.app.tools.SharedPreferenceManager
import ge.gogichaishvili.lotto.main.presentation.viewmodels.base.BaseViewModel

class AdmobViewModel(
    private val pref: SharedPreferenceManager
) : BaseViewModel() {

    fun saveNewBalance (balance: Int) {
        val oldBalance = pref.getPlayerBalance()
        val newBalance = oldBalance + balance
        pref.savePlayerBalance(newBalance)
    }

    fun getNewBalance(): String {
        return pref.getPlayerBalance().toString()
    }
}