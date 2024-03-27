package ge.gogichaishvili.lotto.main.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ge.gogichaishvili.lotto.main.presentation.viewmodels.base.BaseViewModel

class TopUsersViewModel (
) : BaseViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

}