package ge.gogichaishvili.lotto.main.helpers

import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.main.models.OpponentAvatarModel
import java.util.Random

class OpponentManager() {

    private val manPlayers = listOf(
        R.string.giorgi,
        R.string.dato,
        R.string.irakli,
        R.string.nika,
        R.string.ilia,
        R.string.beka,
        R.string.tornike,
        R.string.luka,
        R.string.andria,
        R.string.lasha
    )

    private val womanPlayers = listOf(
        R.string.ana,
        R.string.mari,
        R.string.eka,
        R.string.salome,
        R.string.lili,
        R.string.elene,
        R.string.lizi,
        R.string.natia,
        R.string.tea,
        R.string.tatia
    )

    fun getOpponentInfo(): OpponentAvatarModel {

        val playerId = Random().nextInt(10) + 1

        val isMan = playerId % 2 != 0

        val name = if (isMan) {
            manPlayers[playerId - 1]
        } else {
            womanPlayers[playerId - 1]
        }

        val avatarImage = when (playerId) {
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
        return OpponentAvatarModel(name, avatarImage)
    }

}