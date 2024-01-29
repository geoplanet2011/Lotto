package ge.gogichaishvili.lotto.main.helpers

import ge.gogichaishvili.lotto.main.models.LottoStonesModel

object LottoStonesManager {

    private var bag = (1..90).toMutableList()
    private var lottoNumbers: MutableList<Int> = ArrayList()
    private var bagModel = LottoStonesModel(false, lottoNumbers)
    private const val maxNumber = 3

    fun getNumberFromBag(): LottoStonesModel {

        bag.shuffle()

        /*bag.forEach {
            println("shuffle bag $it")
        }*/

        if (bag.isEmpty()) {
            bagModel.isEmpty = true
        } else {
            val random = bag.random()

            try {
                bag.remove(random)
                bagModel.lottoNumbers.add(random)
                if (bagModel.lottoNumbers.size >= maxNumber) {
                    bagModel.lottoNumbers.removeAt(0)
                }
            } catch (e: Exception) {
                print(e.message)
            }
        }

        return bagModel
    }


   /* fun resetBag () {
        bag.clear()
        bag = (1..90).toMutableList()
        lottoNumbers.clear()
        bagModel.isFinish = false
        bagModel.lottoNumbers.clear()
    }*/

}