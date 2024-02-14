package ge.gogichaishvili.lotto.settings.presentation.viewmodels

import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.SharedPreferenceManager
import ge.gogichaishvili.lotto.main.models.PlayerData
import ge.gogichaishvili.lotto.main.presentation.viewmodels.base.BaseViewModel

class SettingsViewModel(
    private val pref: SharedPreferenceManager
) : BaseViewModel() {

    private var counter: Int = 1

    fun getPlayerInfo(): PlayerData {
        counter = pref.getPlayerInfo().avatarId
        return pref.getPlayerInfo()
    }

    fun nextAvatar() {
        counter++
        if (counter > 10) {
            counter = 1
        }
    }

    fun previousAvatar() {
        counter--
        if (counter < 1) {
            counter = 10
        }
    }

    fun getAvatar(): Int {
        return getAvatarImage(counter)
    }

    fun savePlayerInfo(playerName: String) {
        pref.saveInfo(
            playerName,
            counter,
            getAvatarImage(counter)
        )
    }

    private fun getAvatarImage(counter: Int): Int {
        val avatarImage = when (counter) {
            1 -> R.drawable.avatar_1
            2 -> R.drawable.avatar_2
            3 -> R.drawable.avatar_3
            4 -> R.drawable.avatar_4
            5 -> R.drawable.avatar_5
            6 -> R.drawable.avatar_6
            7 -> R.drawable.avatar_7
            8 -> R.drawable.avatar_8
            9 -> R.drawable.avatar_9
            else -> R.drawable.avatar_10
        }
        return avatarImage
    }

    fun isHintEnabled(): Boolean {
        return pref.isEnabledHint()
    }

    fun isSoundEnabled(): Boolean {
        return pref.isEnabledSound()
    }

    fun onHintChanged(isHintEnabled: Boolean) {
        pref.setIsEnabledHint(isHintEnabled)
    }

    fun onSoundChanged(isSoundEnabled: Boolean) {
        pref.setIsEnabledSound(isSoundEnabled)
    }

    fun getSelectedLanguage(): String {
        return pref.getSelectedLanguageCode()
    }

    fun onLanguageChanged(languageKey: String) {
        pref.saveSelectedLanguageCode(languageKey)
    }

    fun getGameSpeed(): Long {
        return pref.getGameSpeed()
    }

    fun onGameSpeedChanged(speed: Long) {
        pref.setGameSpeed(speed)
    }

}