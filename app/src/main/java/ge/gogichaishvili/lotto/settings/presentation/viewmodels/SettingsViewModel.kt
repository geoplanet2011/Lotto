package ge.gogichaishvili.lotto.settings.presentation.viewmodels

import androidx.lifecycle.LiveData
import ge.gogichaishvili.lotto.app.network.Resourece
import ge.gogichaishvili.lotto.app.tools.SharedPreferenceManager
import ge.gogichaishvili.lotto.app.tools.SingleLiveEvent
import ge.gogichaishvili.lotto.app.viewmodels.base.BaseViewModel

class SettingsViewModel(private val pref: SharedPreferenceManager) : BaseViewModel() {

   /* private val _requestStateLiveData = SingleLiveEvent<Resourece<String>>()
    val requestStateLiveData: LiveData<Resourece<String>> get() = _requestStateLiveData*/

    //fingerprint
    fun onBiometricSwitchChange(biometricStatus: Boolean) {
        saveBiometricStatus(biometricStatus) //save biometric status
    }

    //save current biometric status to DB
    private fun saveBiometricStatus(isBiometricEnabled: Boolean) {
        pref.setIsBiometric(isBiometricEnabled)
    }

    //get current biometric status from db
    fun getBiometricStatus(): Boolean {
        return pref.isBiometric()
    }

}

