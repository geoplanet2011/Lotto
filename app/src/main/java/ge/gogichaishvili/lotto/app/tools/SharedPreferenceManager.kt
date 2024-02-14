package ge.gogichaishvili.lotto.app.tools

import android.content.Context
import android.content.SharedPreferences
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.main.models.GameStatistics
import ge.gogichaishvili.lotto.main.models.PlayerData

class SharedPreferenceManager(context: Context) {

    private val preference: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = preference.edit()

    fun savePlayerInfo(
        nickName: String?,
        avatarId: Int,
        avatar: Int,
        rating: Int,
        balance: Int
    ) {
        editor.apply {
            putString(NICK_NAME_KEY, nickName)
            putInt(AVATAR_ID_KEY, avatarId)
            putInt(AVATAR_KEY, avatar)
            putInt(RATING_KEY, rating)
            putInt(BALANCE_KEY, balance)
        }.apply()
    }

    fun getPlayerInfo(): PlayerData {
        val nickName = preference.getString(NICK_NAME_KEY, "Player")
        val avatarId = preference.getInt(AVATAR_ID_KEY, 1)
        val avatar = preference.getInt(AVATAR_KEY, R.drawable.avatar_1)
        val rating = preference.getInt(RATING_KEY, 0)
        val balance = preference.getInt(BALANCE_KEY, 1000)
        return PlayerData(
            nickName = nickName,
            avatarId = avatarId,
            avatar = avatar,
            rating = rating,
            balance = balance
        )
    }

    fun saveInfo(
        nickName: String?,
        avatarId: Int,
        avatar: Int
    ) {
        editor.apply {
            putString(NICK_NAME_KEY, nickName)
            putInt(AVATAR_ID_KEY, avatarId)
            putInt(AVATAR_KEY, avatar)
        }.apply()
    }

    fun saveGameStatistics(win: Int, lose: Int) {
        editor.apply {
            putInt(WIN_KEY, win)
            putInt(LOSE_KEY, lose)
        }.apply()
    }

    fun getGameStatistics(): GameStatistics {
        val win = preference.getInt(WIN_KEY, 0)
        val lose = preference.getInt(LOSE_KEY, 0)
        return GameStatistics(
            win = win,
            lose = lose
        )
    }

    fun savePlayerBalance(balance: Int) {
        editor.putInt(BALANCE_KEY, balance).apply()
    }

    fun clearAll() {
        editor.clear()
        editor.apply()
    }

    fun saveSelectedLanguageCode(languageKey: String) {
        editor.apply {
            putString(SELECTED_LANGUAGE_KEY, languageKey)
        }.apply()
    }

    fun getSelectedLanguageCode(): String {
        return preference.getString(SELECTED_LANGUAGE_KEY, "en") ?: "en"
    }

    fun isEnabledHint(): Boolean {
        return preference.getBoolean(IS_ENABLED_HINT_KEY, true)
    }

    fun setIsEnabledHint(isEnabledHint: Boolean) {
        editor.apply {
            putBoolean(IS_ENABLED_HINT_KEY, isEnabledHint)
        }.apply()
    }

    fun isEnabledSound(): Boolean {
        return preference.getBoolean(IS_ENABLED_SOUND_KEY, true)
    }

    fun setIsEnabledSound(isEnabledSound: Boolean) {
        editor.apply {
            putBoolean(IS_ENABLED_SOUND_KEY, isEnabledSound)
        }.apply()
    }

    fun getGameSpeed(): Long {
        return preference.getLong(GAME_SPEED_KEY, 2000)
    }

    fun setGameSpeed(gameSpeed: Long) {
        editor.apply {
            putLong(GAME_SPEED_KEY, gameSpeed)
        }.apply()
    }

    companion object {
        private const val PREFERENCE_NAME = "MySharedPreference"
        private const val NICK_NAME_KEY = "Player"
        private const val AVATAR_ID_KEY = "1"
        private const val AVATAR_KEY = "2131165305"
        private const val RATING_KEY = "0"
        private const val BALANCE_KEY = "1000"

        private const val WIN_KEY = "Win"
        private const val LOSE_KEY = "Lose"

        private const val SELECTED_LANGUAGE_KEY = "en"
        private const val IS_ENABLED_HINT_KEY = "false"
        private const val IS_ENABLED_SOUND_KEY = "true"
        private const val GAME_SPEED_KEY = "medium"
    }
}