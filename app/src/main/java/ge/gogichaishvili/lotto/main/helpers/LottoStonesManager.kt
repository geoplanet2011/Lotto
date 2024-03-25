package ge.gogichaishvili.lotto.main.helpers

import ge.gogichaishvili.lotto.main.models.LottoDrawResult

object LottoStonesManager {

    private var bag = (1..90).toMutableList()
    private var drawnNumbers: MutableList<Int> = ArrayList()
    private const val MAX_NUMBER = 3

    fun getNumberFromBag(): LottoDrawResult {
        if (bag.isEmpty()) return LottoDrawResult(isEmpty = true)
        bag.shuffle()
        val number = bag.random().also { bag.remove(it) }
        drawnNumbers.add(number)
        if (drawnNumbers.size > MAX_NUMBER) {
            drawnNumbers.removeAt(0)
        }
        return LottoDrawResult(isEmpty = false, numbers = drawnNumbers.toList())
    }

    fun resetBag() {
        bag = (1..90).toMutableList()
        drawnNumbers.clear()
    }

    fun shuffle() {
        if (bag.isNotEmpty())
            bag.shuffle()
    }
}
