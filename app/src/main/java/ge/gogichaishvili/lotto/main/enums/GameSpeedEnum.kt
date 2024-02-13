package ge.gogichaishvili.lotto.main.enums

enum class GameSpeedEnum (val value: Int) {

    LOW(3000),
    MEDIUM(2000),
    HIGH(1500);

    companion object {
        fun getEnumByCode(code: Int): GameSpeedEnum {
            return when (code) {
                LOW.value -> LOW
                MEDIUM.value -> MEDIUM
                HIGH.value -> HIGH
                else -> throw IllegalStateException()
            }
        }
    }
}