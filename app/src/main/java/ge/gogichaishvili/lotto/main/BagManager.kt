package ge.gogichaishvili.lotto.main

import ge.gogichaishvili.lotto.main.data.BagModel

object BagManager {

    private val bag = (1..90).toMutableList()     //create bag (with all numbers)
    private var lottoNumbers: MutableList<Int> = ArrayList()
    private var bagModel = BagModel(false, lottoNumbers)

    fun getNumberFromBag(): BagModel {

        bag.shuffle()  //shuffle bag

        /*bag.forEach {
            println("shuffle bag $it")
        }*/

        if (bag.isNullOrEmpty()) {
            bagModel.isFinish = true
        } else {
            bagModel.isFinish = false
            val random = bag.random() //get random number

            try {
                bag.remove(random)  //remove random number from bag
                //add random number in lottoNumber list
                bagModel.lottoNumbers.add(random)
                if (bagModel.lottoNumbers.size >= 5) {
                    bagModel.lottoNumbers.removeAt(0)
                }
            } catch (e: Exception) {
                print(e.message)
            }
        }
        return bagModel
    }
}