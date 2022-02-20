package ge.gogichaishvili.lotto.main.helpers

import ge.gogichaishvili.lotto.main.data.LottoStonesModel

object LottoStonesManager {

    private var bag = (1..90).toMutableList()     //create bag (with all numbers)
    private var lottoNumbers: MutableList<Int> = ArrayList()
    private var bagModel = LottoStonesModel(false, lottoNumbers)
    private const val maxNumber = 3

    fun getNumberFromBag(): LottoStonesModel {

        bag.shuffle()  //shuffle bag

        /*bag.forEach {
            println("shuffle bag $it")
        }*/

        if (bag.isNullOrEmpty()) {
            bagModel.isFinish = true
        } else {
            val random = bag.random() //get random number

            try {
                bag.remove(random)  //remove random number from bag
                //add random number in lottoNumber list
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


    fun resetBag () {
        bag.clear()
        bag = (1..90).toMutableList()
        lottoNumbers.clear()
        bagModel.isFinish = false
        bagModel.lottoNumbers.clear()
    }

}