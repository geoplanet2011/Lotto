package ge.gogichaishvili.lotto.main.models

import ge.gogichaishvili.lotto.main.enums.RoomSateEnums
import java.util.Date

data class Room (
    val name: String? = "",
    val locked: Boolean? = false,
    val password: String? = "",
    val state: RoomSateEnums? = RoomSateEnums.OPEN,
    val money: String? = "0",
    val players: MutableList<String> = mutableListOf(),
    val commands: Map<String, String>? = null,
    val createdAt: Long = Date().time,
    val stones: MutableList<Int>? = mutableListOf()
)
