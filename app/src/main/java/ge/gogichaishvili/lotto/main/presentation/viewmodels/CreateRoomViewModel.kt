package ge.gogichaishvili.lotto.main.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ge.gogichaishvili.lotto.main.presentation.viewmodels.base.BaseViewModel

class CreateRoomViewModel (
) : BaseViewModel() {

    private val _inputText = MutableLiveData<String>()
    val inputText: LiveData<String> = _inputText

    fun onTextChanged(text: String) {
        _inputText.value = text
    }

}