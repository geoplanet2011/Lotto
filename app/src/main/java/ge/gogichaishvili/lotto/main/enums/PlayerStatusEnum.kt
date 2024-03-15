package ge.gogichaishvili.lotto.main.enums

enum class PlayerStatusEnum(val value: Int) {

    CREATOR(1),
    JOINER(2),
    UNKNOWN(3);

    companion object {
        fun getEnumByCode(code: Int): PlayerStatusEnum {
            return when (code) {
                CREATOR.value -> CREATOR
                JOINER.value -> JOINER
                UNKNOWN.value -> UNKNOWN
                else -> throw IllegalStateException()
            }
        }
    }
}