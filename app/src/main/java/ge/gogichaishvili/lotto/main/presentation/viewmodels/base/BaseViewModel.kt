package ge.gogichaishvili.lotto.main.presentation.viewmodels.base

import android.content.ComponentCallbacks
import android.content.res.Configuration
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel(), ComponentCallbacks {

    override fun onLowMemory() {}

    override fun onConfigurationChanged(newConfig: Configuration) {}
}