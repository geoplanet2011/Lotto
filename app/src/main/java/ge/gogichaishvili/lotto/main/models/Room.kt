package ge.gogichaishvili.lotto.main.models

import ge.gogichaishvili.lotto.main.enums.RoomSateEnums

data class Room (
    var name: String? = "",
    var isLocked: Boolean? = false,
    var password: String? = "",
    var state: RoomSateEnums? = RoomSateEnums.OPEN
)
