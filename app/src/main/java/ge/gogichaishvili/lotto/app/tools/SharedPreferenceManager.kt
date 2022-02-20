package ge.gogichaishvili.lotto.app.tools

import android.content.Context
import android.content.SharedPreferences
import ge.gogichaishvili.lotto.main.data.GameTypeEnums
import ge.gogichaishvili.lotto.main.data.User

class SharedPreferenceManager(context: Context) {

    private val preference: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = preference.edit()

    //get isFirstLaunch
    fun isFirstLaunch(): Boolean {
        return preference.getBoolean(IS_FIRST_LAUNCH, true)
    }

    //set isFirstLaunch
    fun setIsFirstLaunch(isFirst: Boolean) {
        editor.apply {
            putBoolean(IS_FIRST_LAUNCH, isFirst)
        }.apply()
    }


    //get vibration
    fun isVibrationEnabled(): Boolean {
        return preference.getBoolean(IS_VIBRATION_ENABLED, true)
    }

    //set vibration
    fun setIsVibrationEnabled(isVibrationEnabled: Boolean) {
        editor.apply {
            putBoolean(IS_VIBRATION_ENABLED, isVibrationEnabled)
        }.apply()
    }


    //get sound
    fun isSoundEnabled(): Boolean {
        return preference.getBoolean(IS_SOUND_ENABLED, true)
    }

    //set sound
    fun setIsSoundEnabled(isSoundEnabled: Boolean) {
        editor.apply {
            putBoolean(IS_SOUND_ENABLED, isSoundEnabled)
        }.apply()
    }


    //get music
    fun isMusicEnabled(): Boolean {
        return preference.getBoolean(IS_MUSIC_ENABLED, false)
    }

    //set music
    fun setIsMusicEnabled(isMusicEnabled: Boolean) {
        editor.apply {
            putBoolean(IS_MUSIC_ENABLED, isMusicEnabled)
        }.apply()
    }


    //save language
    fun saveSelectedLanguageCode(languageKey: String) {
        editor.apply {
            putString(SELECTED_LANGUAGE_KEY, languageKey)
        }.apply()
    }

    //get language
    fun getSelectedLanguageCode(): String {
        return preference.getString(SELECTED_LANGUAGE_KEY, "ka") ?: "ka"
    }


    //get dark mode
    fun isDarkMode(): Boolean {
        return preference.getBoolean(IS_DARK_MODE_ENABLED, false)
    }

    //set dark mode
    fun setIsDarkMode(isDarkMode: Boolean) {
        editor.apply {
            putBoolean(IS_DARK_MODE_ENABLED, isDarkMode)
        }.apply()
    }


    //set username
    fun saveUserName(userName: String) {
        editor.apply {
            putString(PREFERENCE_USERNAME, userName)
        }.apply()
    }

    //get Username
    fun getUserName(): String {
        return preference.getString(PREFERENCE_USERNAME, null)!!
    }


    //get biometric auth
    fun isBiometric(): Boolean {
        return preference.getBoolean(IS_BIOMETRIC_ENABLED, false)
    }

    //set biometric auth
    fun setIsBiometric(isBiometricEnabled: Boolean) {
        editor.apply {
            putBoolean(IS_BIOMETRIC_ENABLED, isBiometricEnabled)
        }.apply()
    }


    //get user remember
    fun isUserRemember(): Boolean {
        return preference.getBoolean(IS_USER_REMEMBER_ENABLED, false)
    }

    //set user remember
    fun setIsUserRemember(isUserRememberEnabled: Boolean) {
        editor.apply {
            putBoolean(IS_USER_REMEMBER_ENABLED, isUserRememberEnabled)
        }.apply()
    }


    //save user info
    fun saveUserInfo(id: String?, firstName: String?, lastName: String?, phoneNumber: String?, photo: String?, email: String?, avatar: String?, money: Int) {
        editor.apply {
            putString(CUSTOMER_ID_KEY, id)
            putString(FIRST_NAME_KEY, firstName)
            putString(LAST_NAME_KEY, lastName)
            putString(PHONE_NUMBER_KEY, phoneNumber)
            putString(PHOTO_KEY, photo)
            putString(EMAIL_KEY, email)
            putString(AVATAR_KEY, avatar)
            putInt(START_MONEY_KEY, money)

        }.apply()
    }

    //get user info
    fun getUserData(): User {
        val id = preference.getString(CUSTOMER_ID_KEY, null)
        val firstName = preference.getString(FIRST_NAME_KEY, null)
        val lastName = preference.getString(LAST_NAME_KEY, null)
        val phoneNumber = preference.getString(PHONE_NUMBER_KEY, null)
        val photo = preference.getString(PHOTO_KEY, null)
        val email = preference.getString(EMAIL_KEY, null)
        val avatar = preference.getString(AVATAR_KEY, null)
        val money = preference.getInt(START_MONEY_KEY, 1000)

        return User(
            id = id,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            photo = photo,
            email = email,
            avatar = avatar,
            money = money
        )
    }


    //save game speed
    fun saveGameSpeed(speed: Int) {
        editor.apply {
            putInt(GAME_SPEED, speed)
        }.apply()
    }

    //get game speed
    fun getGameSpeed(): Int {
        return preference.getInt(GAME_SPEED, 3)
    }


    //save game type
    fun saveGameType(type: GameTypeEnums) {
        editor.apply {
            putInt(GAME_TYPE, type.value)
        }.apply()
    }

    //get game type
    fun getGameType(): GameTypeEnums? {
        val typeName = preference.getInt(GAME_TYPE, 2)
        return GameTypeEnums.getEnumByCode(typeName)
    }


    //save number of cards
    fun saveNumberOfCards(number: Int) {
        editor.apply {
            putInt(NUMBER_OF_CARDS, number)
        }.apply()
    }

    //get number of cards
    fun getNumberOfCards(): Int {
        return preference.getInt(NUMBER_OF_CARDS, 3)
    }


    //save money
    fun saveMoney(money: Int) {
        editor.apply {
            putInt(START_MONEY, money)
        }.apply()
    }

    //get money
    fun getMoney(): Int {
        return preference.getInt(START_MONEY, 1000)
    }


    //save max number of lotto stones
    fun saveMaxNumberOfLottoStones(number: Int) {
        editor.apply {
            putInt(MAX_NUMBER_OF_LOTTO_STONES, number)
        }.apply()
    }

    //get max number of lotto stones
    fun getMaxNumberOfLottoStones(): Int {
        return preference.getInt(MAX_NUMBER_OF_LOTTO_STONES, 3)
    }


    //Clear all
    fun clearAll() {
        editor.clear()
        editor.apply()
    }


    companion object {
        private const val PREFERENCE_NAME = "MySharedPreference"
        private const val IS_FIRST_LAUNCH = "is first launch"
        private const val IS_VIBRATION_ENABLED = "is_vibration_enabled"
        private const val IS_SOUND_ENABLED = "is_sound_enabled"
        private const val IS_MUSIC_ENABLED = "is_music_enabled"
        private const val SELECTED_LANGUAGE_KEY = "selected_language_key"
        private const val IS_DARK_MODE_ENABLED = "is_dark_mode_enabled"
        private const val PREFERENCE_USERNAME = "Username"
        private const val IS_BIOMETRIC_ENABLED = "is_biometric_enabled"
        private const val IS_USER_REMEMBER_ENABLED = "is_user_remember_enabled"
        private const val GAME_SPEED = "game_speed"
        private const val GAME_TYPE = "game_type"
        private const val NUMBER_OF_CARDS = "number_of_cards"
        private const val START_MONEY = "start_money"
        private const val MAX_NUMBER_OF_LOTTO_STONES = "max_number_of_lotto_stones"

        private const val CUSTOMER_ID_KEY = "customer_id"
        private const val FIRST_NAME_KEY = "firstName"
        private const val LAST_NAME_KEY = "lastname"
        private const val PHONE_NUMBER_KEY = "phonenumber"
        private const val PHOTO_KEY = "photo"
        private const val AVATAR_KEY = "avatar"
        private const val EMAIL_KEY = "email"
        private const val START_MONEY_KEY = "start_money_key"

    }

}