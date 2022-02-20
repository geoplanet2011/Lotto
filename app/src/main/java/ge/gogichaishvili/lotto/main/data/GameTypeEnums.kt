package ge.gogichaishvili.lotto.main.data

enum class GameTypeEnums (val value: Int) {

    SHORT(1),
    LONG(2);

    companion object {
        fun getEnumByCode(code: Int): GameTypeEnums? {
            return when (code) {
                SHORT.value -> SHORT
                LONG.value -> LONG
                else -> null
            }
        }
    }
}