package ge.gogichaishvili.lotto.main.enums

enum class RoomSateEnums(val value: Int) {

    OPEN(1),
    CLOSE(2),
    FINISH(3);

    companion object {
        fun RoomSateEnums(code: Int): RoomSateEnums {
            return when (code) {
                OPEN.value -> OPEN
                CLOSE.value -> CLOSE
                FINISH.value -> FINISH
                else -> throw IllegalStateException()
            }
        }
    }
}