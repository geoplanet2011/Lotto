package ge.gogichaishvili.lotto.main.helpers

import android.content.Context
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.main.models.OpponentAvatarModel
import java.util.Random

class OpponentManager(context: Context) {

    private val manPlayers = listOf(
        context.getString(R.string.giorgi),
        context.getString(R.string.dato),
        context.getString(R.string.irakli),
        context.getString(R.string.nika),
        context.getString(R.string.ilia),
        context.getString(R.string.beka),
        context.getString(R.string.tornike),
        context.getString(R.string.luka),
        context.getString(R.string.andria),
        context.getString(R.string.lasha)
    )

    private val womanPlayers = listOf(
        context.getString(R.string.ana),
        context.getString(R.string.mari),
        context.getString(R.string.eka),
        context.getString(R.string.salome),
        context.getString(R.string.lili),
        context.getString(R.string.elene),
        context.getString(R.string.lizi),
        context.getString(R.string.natia),
        context.getString(R.string.tea),
        context.getString(R.string.tatia)
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