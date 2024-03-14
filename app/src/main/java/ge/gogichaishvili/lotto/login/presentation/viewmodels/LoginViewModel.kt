package ge.gogichaishvili.lotto.login.presentation.viewmodels

import ge.gogichaishvili.lotto.app.tools.SharedPreferenceManager
import ge.gogichaishvili.lotto.main.presentation.viewmodels.base.BaseViewModel

class LoginViewModel(
    private val pref: SharedPreferenceManager
) : BaseViewModel() {

    fun onUserRememberRadioButtonChange(userRememberStatus: Boolean, userName: String) {
        saveUserRememberStatus(userRememberStatus)
        saveUserName(userName)
    }

    private fun saveUserRememberStatus(isUserRememberEnabled: Boolean) {
        pref.setIsUserRemember(isUserRememberEnabled)
    }

    fun getUserRememberStatus(): Boolean {
        return pref.isUserRemember()
    }

    private fun saveUserName(userName: String) {
        pref.saveUserName(userName)
    }

    fun getUserName(): String {
        return pref.getUserName()
    }

}